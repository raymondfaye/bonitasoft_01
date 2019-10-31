package org.bonitasoft.engine.sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.bonitasoft.engine.lock.BonitaLock;
import org.bonitasoft.engine.lock.LockService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TenantSequenceManagerImplTest {


    private static final long TENANT_ID = 12L;
    private static final String OBJECT_w_2 = "OBJECT_w_2";
    private static final String OBJECT_w_5 = "OBJECT_w_5";
    private static final String OBJECT_w_100 = "OBJECT_w_100";
    private static final String OBJECT_w_1000 = "OBJECT_w_1000";
    private static final long SEQUENCE_w_2 = 553L;
    private static final long SEQUENCE_w_5 = 554L;
    private static final long SEQUENCE_w_100 = 555L;
    private static final long SEQUENCE_w_1000 = 556L;
    private static final int RETRIES = 2;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    private TenantSequenceManagerImpl tenantSequenceManager;
    @Mock
    private SequenceDAO sequenceDAO;
    @Mock
    private LockService lockService;
    @Mock
    private BonitaLock lock;
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;


    @Before
    public void before() throws Exception {
        doReturn(connection).when(dataSource).getConnection();
        HashMap<Long, Integer> sequenceIdToRangeSize = new HashMap<>();
        sequenceIdToRangeSize.put(SEQUENCE_w_2, 2);
        sequenceIdToRangeSize.put(SEQUENCE_w_5, 5);
        sequenceIdToRangeSize.put(SEQUENCE_w_100, 100);
        sequenceIdToRangeSize.put(SEQUENCE_w_1000, 1000);
        HashMap<String, Long> classNameToSequenceId = new HashMap<>();
        classNameToSequenceId.put(OBJECT_w_2, SEQUENCE_w_2);
        classNameToSequenceId.put(OBJECT_w_5, SEQUENCE_w_5);
        classNameToSequenceId.put(OBJECT_w_100, SEQUENCE_w_100);
        classNameToSequenceId.put(OBJECT_w_1000, SEQUENCE_w_1000);

        doReturn(lock).when(lockService).lock(anyLong(), anyString(), anyLong());

        tenantSequenceManager = new TenantSequenceManagerImpl(TENANT_ID, lockService, sequenceIdToRangeSize,
                classNameToSequenceId, dataSource, RETRIES, 1, 1) {
            @Override
            SequenceDAO createDao(Connection connection, Long tenantId) {
                return sequenceDAO;
            }
        };
    }

    @Test
    public void should_get_next_id_from_database() throws Exception {
        doReturn(100L).when(sequenceDAO).selectById(SEQUENCE_w_100);

        long nextId = tenantSequenceManager.getNextId(OBJECT_w_100);

        assertThat(nextId).isEqualTo(100);
    }

    @Test
    public void should_get_next_id_once_all_ids_is_range_are_used() throws Exception {
        doReturn(100L, 200L).when(sequenceDAO).selectById(SEQUENCE_w_5);

        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(100);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(101);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(102);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(103);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(104);

        //all ids are taken, ask for new ones
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(200);
    }


    @Test
    public void should_get_next_id_once_all_ids_is_range_are_used_for_each_object() throws Exception {
        doReturn(100L, 200L).when(sequenceDAO).selectById(SEQUENCE_w_5);
        doReturn(1100L, 1200L).when(sequenceDAO).selectById(SEQUENCE_w_2);

        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(100);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_2)).isEqualTo(1100);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(101);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_2)).isEqualTo(1101);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(102);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_2)).isEqualTo(1200);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(103);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(104);
        assertThat(tenantSequenceManager.getNextId(OBJECT_w_5)).isEqualTo(200);
    }

    @Test
    public void should_update_sequence_inside_a_lock() throws Exception {
        doReturn(100L).when(sequenceDAO).selectById(SEQUENCE_w_5);
        InOrder inOrder = inOrder(lockService, connection, sequenceDAO);

        tenantSequenceManager.getNextId(OBJECT_w_5);

        inOrder.verify(lockService).lock(SEQUENCE_w_5, TenantSequenceManagerImpl.SEQUENCE, TENANT_ID);
        inOrder.verify(sequenceDAO).selectById(SEQUENCE_w_5);
        inOrder.verify(sequenceDAO).updateSequence(105L, SEQUENCE_w_5);
        inOrder.verify(connection).commit();
        inOrder.verify(lockService).unlock(lock, TENANT_ID);
    }

    @Test
    public void should_update_sequence_with_id_of_next_range() throws Exception {
        doReturn(100L).when(sequenceDAO).selectById(SEQUENCE_w_5);
        doReturn(100L).when(sequenceDAO).selectById(SEQUENCE_w_1000);

        tenantSequenceManager.getNextId(OBJECT_w_5);
        tenantSequenceManager.getNextId(OBJECT_w_1000);

        verify(sequenceDAO).updateSequence(105L, SEQUENCE_w_5);
        verify(sequenceDAO).updateSequence(1100L, SEQUENCE_w_1000);
    }

    @Test
    public void should_retry_to_update_sequence_when_select_of_next_id_fails() throws Exception {
        doThrow(new SQLException("SQL error")).doReturn(200L).when(sequenceDAO).selectById(SEQUENCE_w_5);

        long nextId = tenantSequenceManager.getNextId(OBJECT_w_5);

        assertThat(nextId).isEqualTo(200L);
    }
    @Test
    public void should_retry_to_update_sequence_when_update_of_next_range_fails() throws Exception {
        doReturn(200L).when(sequenceDAO).selectById(SEQUENCE_w_5);
        doThrow(new SQLException("SQL error")).doNothing().when(sequenceDAO).updateSequence(205, SEQUENCE_w_5);

        long nextId = tenantSequenceManager.getNextId(OBJECT_w_5);

        assertThat(nextId).isEqualTo(200L);
    }
    @Test
    public void should_retry_to_update_sequence_when_commit_fails() throws Exception {
        doReturn(200L).when(sequenceDAO).selectById(SEQUENCE_w_5);
        doNothing().when(sequenceDAO).updateSequence(205, SEQUENCE_w_5);
        doThrow(new SQLException("commit error")).doNothing().when(connection).commit();

        long nextId = tenantSequenceManager.getNextId(OBJECT_w_5);

        assertThat(nextId).isEqualTo(200L);
    }


    @Test
    public void should_fail_to_update_sequence_when_select_of_next_id_fails() throws Exception {
        doThrow(new SQLException("SQL error")).doThrow(new SQLException("SQL error")).doReturn(200L).when(sequenceDAO).selectById(SEQUENCE_w_5);

        assertThatThrownBy(() -> tenantSequenceManager.getNextId(OBJECT_w_5))
                .hasMessage("Unable to get a sequence id for 554");
    }
    @Test
    public void should_fail_to_update_sequence_when_update_of_next_range_fails() throws Exception {
        doReturn(200L).when(sequenceDAO).selectById(SEQUENCE_w_5);
        doThrow(new SQLException("SQL error")).doThrow(new SQLException("SQL error")).doNothing().when(sequenceDAO).updateSequence(205, SEQUENCE_w_5);


        assertThatThrownBy(() -> tenantSequenceManager.getNextId(OBJECT_w_5))
                .hasMessage("Unable to get a sequence id for 554");
    }
    @Test
    public void should_fail_to_update_sequence_when_commit_fails() throws Exception {
        doReturn(200L).when(sequenceDAO).selectById(SEQUENCE_w_5);
        doNothing().when(sequenceDAO).updateSequence(205, SEQUENCE_w_5);
        doThrow(new SQLException("commit error")).doThrow(new SQLException("commit error")).doNothing().when(connection).commit();


        assertThatThrownBy(() -> tenantSequenceManager.getNextId(OBJECT_w_5))
                .hasMessage("Unable to get a sequence id for 554");
    }


    @Test
    public void should_not_increase_next_id_when_we_are_unable_to_commit_transaction() throws Exception {
        doReturn(200L).when(sequenceDAO).selectById(SEQUENCE_w_5);
        doNothing().when(sequenceDAO).updateSequence(205, SEQUENCE_w_5);
        doThrow(new SQLException("commit error")).when(connection).commit();

        assertThatThrownBy(() -> tenantSequenceManager.getNextId(OBJECT_w_5)).hasMessage("Unable to get a sequence id for 554");

        // should also fail and not return 200 (saved in memory)
        assertThatThrownBy(() -> tenantSequenceManager.getNextId(OBJECT_w_5)).hasMessage("Unable to get a sequence id for 554");
    }

}