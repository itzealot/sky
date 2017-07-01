/*
 * Copyright 2015-2016 Dark Phoenixs (Open-Source Organization).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sky.project.share.common.pool;

import java.io.Closeable;
import java.io.Serializable;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 池基类
 * 
 * @author zealot
 *
 * @param <T>
 */
public abstract class PoolBase<T> implements Closeable, Serializable {
	private static final long serialVersionUID = 7943866996536486402L;

	protected GenericObjectPool<T> internalPool;

	public PoolBase() {
	}

	public PoolBase(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {
		this.initPool(poolConfig, factory);
	}

	/**
	 * 初始化对象池
	 * 
	 * @param poolConfig
	 * @param factory
	 */
	protected void initPool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {
		if (this.internalPool != null)
			this.destroy();
		this.internalPool = new GenericObjectPool<T>(factory, poolConfig);
	}

	/**
	 * 销毁对象池
	 */
	protected void destroy() {
		this.close();
	}

	/**
	 * 获得池对象
	 * 
	 * @return
	 */
	protected T getResource() throws ConnectionException {
		try {
			return internalPool.borrowObject();
		} catch (Exception e) {
			throw new ConnectionException("Could not get a resource from pool", e);
		}
	}

	/**
	 * 返回对象到池中
	 * 
	 * @param resource
	 */
	protected void returnResource(final T resource) throws ConnectionException {
		if (null != resource)
			try {
				internalPool.returnObject(resource);
			} catch (Exception e) {
				throw new ConnectionException("Could not return the resource to pool", e);
			}
	}

	/**
	 * 废弃池对象
	 * 
	 * @param resource
	 */
	protected void invalidateResource(final T resource) {
		if (null != resource)
			try {
				internalPool.invalidateObject(resource);
			} catch (Exception e) {
				throw new ConnectionException("Could not invalidate the resource to pool", e);
			}
	}

	/**
	 * 获得池激活数
	 * 
	 * @return
	 */
	public int getNumActive() {
		return isInactived() ? -1 : this.internalPool.getNumActive();
	}

	/**
	 * 获得池空闲数
	 * 
	 * @return
	 */
	public int getNumIdle() {
		return isInactived() ? -1 : this.internalPool.getNumIdle();
	}

	/**
	 * 获得池等待数
	 * 
	 * @return
	 */
	public int getNumWaiters() {
		return isInactived() ? -1 : this.internalPool.getNumWaiters();
	}

	/**
	 * 获得平均等待时间
	 * 
	 * @return
	 */
	public long getMeanBorrowWaitTimeMillis() {
		return isInactived() ? -1 : this.internalPool.getMeanBorrowWaitTimeMillis();
	}

	/**
	 * 获得最大等待时间
	 * 
	 * @return
	 */
	public long getMaxBorrowWaitTimeMillis() {
		return isInactived() ? -1 : this.internalPool.getMaxBorrowWaitTimeMillis();
	}

	/**
	 * 是否关闭
	 * 
	 * @return
	 */
	public boolean isClosed() throws ConnectionException {
		try {
			return this.internalPool.isClosed();
		} catch (Exception e) {
			throw new ConnectionException("Could not check closed from pool", e);
		}
	}

	/**
	 * 池是否失效
	 * 
	 * @return
	 */
	private boolean isInactived() throws ConnectionException {
		try {
			return this.internalPool == null || this.internalPool.isClosed();
		} catch (Exception e) {
			throw new ConnectionException("Could not check inactived from pool", e);
		}
	}

	/**
	 * 添加池对象
	 * 
	 * @param count
	 */
	protected void addObjects(final int count) throws ConnectionException {
		try {
			for (int i = 0; i < count; i++) {
				this.internalPool.addObject();
			}
		} catch (Exception e) {
			throw new ConnectionException("Error trying to add idle objects", e);
		}
	}

	/**
	 * 清除对象池
	 */
	public void clear() throws ConnectionException {
		try {
			this.internalPool.clear();
		} catch (Exception e) {
			throw new ConnectionException("Could not clear pool", e);
		}
	}

	/**
	 * 关闭对象池
	 */
	public void close() throws ConnectionException {
		try {
			this.internalPool.close();
		} catch (Exception e) {
			throw new ConnectionException("Could not destroy pool", e);
		}
	}
}
