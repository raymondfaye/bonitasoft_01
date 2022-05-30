/**
 * Copyright (C) 2019 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.cache.ehcache;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import org.bonitasoft.engine.cache.CommonCacheService;
import org.bonitasoft.engine.cache.SCacheException;
import org.bonitasoft.engine.commons.LogUtil;
import org.slf4j.Logger;

/**
 * @author Matthieu Chaffotte
 */
public abstract class CommonEhCacheCacheService implements CommonCacheService {

    protected CacheManager cacheManager;

    protected final Map<String, CacheConfiguration> cacheConfigurations;

    private final CacheConfiguration defaultCacheConfiguration;

    private final String diskStorePath;

    private String cacheManagerLastCreation;

    protected abstract Logger getLogger();

    public CommonEhCacheCacheService(final List<org.bonitasoft.engine.cache.CacheConfiguration> cacheConfigurations,
            final org.bonitasoft.engine.cache.CacheConfiguration defaultCacheConfiguration,
            final String diskStorePath) {
        this.diskStorePath = diskStorePath;
        this.defaultCacheConfiguration = getEhCacheConfiguration(defaultCacheConfiguration);
        if (cacheConfigurations != null && cacheConfigurations.size() > 0) {
            this.cacheConfigurations = new HashMap<>(cacheConfigurations.size());
            for (final org.bonitasoft.engine.cache.CacheConfiguration cacheConfig : cacheConfigurations) {
                this.cacheConfigurations.put(cacheConfig.getName(), getEhCacheConfiguration(cacheConfig));
            }
        } else {
            this.cacheConfigurations = Collections.emptyMap();
        }
    }

    protected CacheConfiguration getEhCacheConfiguration(
            final org.bonitasoft.engine.cache.CacheConfiguration cacheConfig) {
        final CacheConfiguration ehCacheConfig = new CacheConfiguration();
        ehCacheConfig.setMaxElementsInMemory(cacheConfig.getMaxElementsInMemory());
        ehCacheConfig.setMaxElementsOnDisk(cacheConfig.getMaxElementsOnDisk());
        ehCacheConfig.setOverflowToDisk(!cacheConfig.isInMemoryOnly());
        ehCacheConfig.setEternal(cacheConfig.isEternal());
        ehCacheConfig.setCopyOnRead(cacheConfig.isCopyOnRead());
        ehCacheConfig.setCopyOnWrite(cacheConfig.isCopyOnWrite());
        if (!cacheConfig.isEternal()) {
            ehCacheConfig.setTimeToLiveSeconds(cacheConfig.getTimeToLiveSeconds());
        }
        return ehCacheConfig;
    }

    protected void buildCacheManagerWithDefaultConfiguration() {
        if (cacheManager == null) {
            final Configuration configuration = new Configuration();
            configuration.setName(getCacheManagerName());
            configuration.setDefaultCacheConfiguration(defaultCacheConfiguration);
            configuration.diskStore(new DiskStoreConfiguration().path(diskStorePath));
            cacheManager = new CacheManager(configuration);
            if (getLogger().isTraceEnabled()) {
                cacheManagerLastCreation = getCacheManagerCreationDetails();
            }
        }
    }

    private String getCacheManagerCreationDetails() {
        final StringBuilder sb = new StringBuilder();
        String identifier = getCacheManagerIdentifier();
        sb.append("CacheManager (").append(cacheManager).append(") built for ").append(identifier);
        sb.append("\n");
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (final StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append("\n        at ");
            sb.append(stackTraceElement);
        }
        return sb.toString();
    }

    protected abstract String getCacheManagerIdentifier();

    protected synchronized Cache createCache(final String cacheName, final String internalCacheName)
            throws SCacheException {
        if (cacheManager == null) {
            throw new SCacheException("The cache is not started, call start() on the cache service");
        }
        Cache cache = cacheManager.getCache(internalCacheName);
        if (cache == null) {
            final CacheConfiguration cacheConfiguration = cacheConfigurations.get(cacheName);
            if (cacheConfiguration != null) {
                final CacheConfiguration newCacheConfig = cacheConfiguration.clone();
                newCacheConfig.setName(internalCacheName);
                cache = new Cache(newCacheConfig);
                cacheManager.addCache(cache);
            } else {
                throw new SCacheException("No configuration found for the cache " + cacheName);
            }
        }
        return cache;
    }

    @Override
    public void store(final String cacheName, final Serializable key, final Object value) throws SCacheException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(
                    LogUtil.getLogBeforeMethod(this.getClass(), "store"));
        }
        if (cacheManager == null) {
            throw new SCacheException("The cache is not started, call start() on the cache service");
        }
        final String cacheNameKey = getKeyFromCacheName(cacheName);
        try {
            Cache cache = cacheManager.getCache(cacheNameKey);
            if (cache == null) {
                cache = createCache(cacheName, cacheNameKey);
            }
            if (value instanceof Serializable) {
                cache.put(new Element(key, (Serializable) value));
            } else {
                cache.put(new Element(key, value));
            }
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogAfterMethod(this.getClass(), "store"));
            }
        } catch (final IllegalStateException e) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "store", e));
            }
            final StringBuilder stringBuilder = new StringBuilder("The cache '");
            stringBuilder.append(cacheNameKey).append("' is not alive");
            final String msg = stringBuilder.toString();
            throw new SCacheException(msg, e);
        } catch (final net.sf.ehcache.CacheException ce) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "store", ce));
            }
            throw new SCacheException(ce);
        }
    }

    protected abstract String getKeyFromCacheName(String cacheName) throws SCacheException;

    @Override
    public Object get(final String cacheName, final Object key) throws SCacheException {
        if (cacheManager == null) {
            return null;
        }
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(LogUtil.getLogBeforeMethod(this.getClass(), "get"));
        }
        final String cacheNameKey = getKeyFromCacheName(cacheName);
        try {
            final Cache cache = cacheManager.getCache(cacheNameKey);
            if (cache == null) {
                // the cache does not exist = the key was not stored
                return null;
            }
            final Element element = cache.get(key);
            if (element != null) {
                if (getLogger().isTraceEnabled()) {
                    getLogger().trace(
                            LogUtil.getLogAfterMethod(this.getClass(), "get"));
                }
                return element.getObjectValue();
            }
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogAfterMethod(this.getClass(), "get"));
            }
            return null;
        } catch (final IllegalStateException e) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "get", e));
            }
            final StringBuilder stringBuilder = new StringBuilder("The cache '");
            stringBuilder.append(cacheNameKey).append("' is not alive");
            final String msg = stringBuilder.toString();
            throw new SCacheException(msg, e);
        } catch (final Exception e) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "get", e));
            }
            final StringBuilder stringBuilder = new StringBuilder("The cache '");
            stringBuilder.append(cacheNameKey).append("' does not exist");
            final String msg = stringBuilder.toString();
            throw new SCacheException(msg, e);
        }
    }

    @Override
    public boolean clear(final String cacheName) throws SCacheException {
        if (cacheManager == null) {
            return true;
        }
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(
                    LogUtil.getLogBeforeMethod(this.getClass(), "clear"));
        }
        final String cacheNameKey = getKeyFromCacheName(cacheName);
        try {
            final Cache cache = cacheManager.getCache(cacheNameKey);
            if (cache != null) {
                cache.removeAll();
            }
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogAfterMethod(this.getClass(), "clear"));
            }
            return cache == null;
        } catch (final IllegalStateException e) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "clear", e));
            }
            final StringBuilder stringBuilder = new StringBuilder("The cache '");
            stringBuilder.append(cacheNameKey).append("' is not alive");
            final String msg = stringBuilder.toString();
            throw new SCacheException(msg, e);
        } catch (final Exception e) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "clear", e));
            }
            final StringBuilder stringBuilder = new StringBuilder("The cache '");
            stringBuilder.append(cacheNameKey).append("' does not exist");
            final String msg = stringBuilder.toString();
            throw new SCacheException(msg, e);
        }
    }

    @Override
    public int getCacheSize(final String cacheName) throws SCacheException {
        if (cacheManager == null) {
            return 0;
        }
        final Cache cache = cacheManager.getCache(getKeyFromCacheName(cacheName));
        if (cache == null) {
            return 0;
        }
        return cache.getSize();
    }

    @Override
    public void clearAll() throws SCacheException {
        if (cacheManager == null) {
            return;
        }
        try {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogBeforeMethod(this.getClass(), "clearAll"));
            }
            final List<String> cacheNames = getCachesNames();
            for (final String cacheName : cacheNames) {
                clear(cacheName);
            }
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogAfterMethod(this.getClass(), "clearAll"));
            }
        } catch (final net.sf.ehcache.CacheException e) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "clearAll", e));
            }
            throw new SCacheException(e);
        }
    }

    @Override
    public boolean remove(final String cacheName, final Object key) throws SCacheException {
        if (cacheManager == null) {
            return false;
        }

        final String cacheNameKey = getKeyFromCacheName(cacheName);
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(
                    LogUtil.getLogBeforeMethod(this.getClass(), "remove"));
        }
        final Cache cache = cacheManager.getCache(cacheNameKey);

        if (getLogger().isTraceEnabled()) {
            getLogger().trace(
                    LogUtil.getLogAfterMethod(this.getClass(), "remove"));
        }
        // key was not removed
        return cache != null && cache.remove(key);
    }

    @Override
    public List<Object> getKeys(final String cacheName) throws SCacheException {
        if (cacheManager == null) {
            return Collections.emptyList();
        }
        final String cacheNameKey = getKeyFromCacheName(cacheName);
        try {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogBeforeMethod(this.getClass(), "getKeys"));
            }
            final Cache cache = cacheManager.getCache(cacheNameKey);
            if (cache == null) {
                return Collections.emptyList();
            }
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogAfterMethod(this.getClass(), "getKeys"));
            }
            return cache.getKeys();
        } catch (final IllegalStateException e) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "getKeys", e));
            }
            final StringBuilder stringBuilder = new StringBuilder("The cache '");
            stringBuilder.append(cacheNameKey).append("' is not alive");
            final String msg = stringBuilder.toString();
            throw new SCacheException(msg, e);
        } catch (final net.sf.ehcache.CacheException e) {
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "getKeys", e));
            }
            throw new SCacheException(e);
        }
    }

    protected void shutdownCacheManager() {
        if (cacheManager != null) {
            cacheManager.shutdown();
            cacheManager = null;
        }
    }

    @Override
    public boolean isStopped() {
        return cacheManager == null;
    }

    protected abstract String getCacheManagerName();
}
