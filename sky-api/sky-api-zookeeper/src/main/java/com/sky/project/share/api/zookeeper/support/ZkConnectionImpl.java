package com.sky.project.share.api.zookeeper.support;

import java.io.Serializable;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.sky.project.share.api.zookeeper.ZkConnection;
import com.sky.project.share.api.zookeeper.ZkPath;
import com.sky.project.share.api.zookeeper.ZkPath.PathFilter;
import com.sky.project.share.common.util.Serializables;

public final class ZkConnectionImpl implements ZkConnection {
	private static final byte[] DEFAULT_DATA = new byte[0];

	CuratorFramework client;

	public ZkConnectionImpl(String connectionString) {
		client = CuratorFrameworkFactory.builder().connectString(connectionString).retryPolicy(new RetryOneTime(100))
				.build();
		client.start();
	}

	public ZkConnectionImpl(String connectionString, String namespace) {
		client = CuratorFrameworkFactory.builder().connectString(connectionString).namespace(namespace)
				.retryPolicy(new RetryOneTime(100)).build();
		client.start();
	}

	@Override
	public ZkPath create() {
		return create("/", null);
	}

	@Override
	public ZkPath create(String path) {
		return create(path, null);
	}

	@Override
	public ZkPath create(String path, byte[] data) {
		return path == null || path.isEmpty() ? null : new ZkPathImpl(this, path, data);
	}

	@Override
	public boolean exists(ZkPath path) {
		return path == null ? false : exists(path.getPath());
	}

	@Override
	public boolean exists(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}

		try {
			return client.checkExists().forPath(path) != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean mkdir(ZkPath path) {
		return path == null ? false : mkdir(path.getPath(), path.getData());
	}

	@Override
	public boolean mkdir(String path) {
		return mkdir(path, DEFAULT_DATA);
	}

	@Override
	public boolean mkdir(String path, byte[] data) {
		if (path == null || path.isEmpty()) {
			return false;
		}

		try {
			client.create().forPath(path, data == null ? DEFAULT_DATA : data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean mkdirs(ZkPath path) {
		return path == null ? false : mkdirs(path.getPath(), path.getData());
	}

	@Override
	public boolean mkdirs(String path) {
		return mkdirs(path, DEFAULT_DATA);
	}

	@Override
	public boolean mkdirs(String path, byte[] data) {
		if (path == null || path.isEmpty()) {
			return false;
		}

		try {
			client.create().creatingParentsIfNeeded().forPath(path, data == null ? DEFAULT_DATA : data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean delete(ZkPath path) {
		return path == null ? false : delete(path.getPath());
	}

	@Override
	public boolean delete(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}

		try {
			client.delete().forPath(path);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean deleteRecursive(ZkPath path) {
		return path == null ? false : deleteRecursive(path.getPath());
	}

	@Override
	public boolean deleteRecursive(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}

		try {
			client.delete().deletingChildrenIfNeeded().forPath(path);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<ZkPath> children(ZkPath path) {
		return path == null ? Lists.newArrayList() : children(path.getPath());
	}

	@Override
	public List<ZkPath> children(String path) {
		if (path == null || path.isEmpty()) {
			return Lists.newArrayList();
		}

		List<ZkPath> paths = Lists.newArrayList();
		List<String> childrens = childrenNames(path);

		String base = path;
		base = "/".equals(base) ? "" : base;

		for (String str : childrens) {
			paths.add(new ZkPathImpl(this, base + "/" + str, DEFAULT_DATA));
		}

		return paths;
	}

	@Override
	public List<ZkPath> children(ZkPath path, PathFilter filter) {
		return path == null ? Lists.newArrayList() : children(path.getPath(), filter);
	}

	@Override
	public List<ZkPath> children(String path, PathFilter filter) {
		List<ZkPath> childrens = children(path);
		if (filter == null) {
			return childrens;
		}

		List<ZkPath> paths = Lists.newArrayList();

		for (ZkPath p : childrens) {
			if (filter.accpet(p.getPath())) {
				paths.add(p);
			}
		}

		return paths;
	}

	@Override
	public List<String> childrenNames(ZkPath path) {
		return path == null ? Lists.newArrayList() : childrenNames(path.getPath());
	}

	@Override
	public List<String> childrenNames(String path) {
		if (path == null || path.isEmpty()) {
			return Lists.newArrayList();
		}
		try {
			return client.getChildren().forPath(path);
		} catch (Exception e) {
			return Lists.newArrayList();
		}
	}

	@Override
	public List<String> childrenNames(ZkPath path, PathFilter filter) {
		return path == null ? Lists.newArrayList() : childrenNames(path.getPath(), filter);
	}

	@Override
	public List<String> childrenNames(String path, PathFilter filter) {
		List<String> paths = childrenNames(path);
		if (filter == null) {
			return paths;
		}

		List<String> results = Lists.newArrayList();

		for (String str : paths) {
			if (filter.accpet(str))
				results.add(str);
		}

		return results;
	}

	@Override
	public byte[] loadData(ZkPath path) {
		if (path == null) {
			return null;
		}

		try {
			byte[] bytes = client.getData().forPath(path.getPath());
			path.setData(bytes);
			return bytes;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public byte[] loadData(String path) {
		if (path == null) {
			return null;
		}

		try {
			return client.getData().forPath(path);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public <T> T loadJson(ZkPath path, Class<T> clazz) {
		if (path == null || clazz == null) {
			return null;
		}

		try {
			byte[] data = client.getData().forPath(path.getPath());
			path.setData(data);

			return new Gson().fromJson(new String(data), clazz);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public <T> T loadJson(String path, Class<T> clazz) {
		if (path == null | clazz == null) {
			return null;
		}
		try {
			return new Gson().fromJson(new String(client.getData().forPath(path)), clazz);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public <T extends Serializable> T loadSerializeData(ZkPath path) {
		if (path == null) {
			return null;
		}

		try {
			byte[] data = client.getData().forPath(path.getPath());
			path.setData(data);
			return Serializables.readObject(data);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public <T extends Serializable> T loadSerializeData(String path) {
		if (path == null) {
			return null;
		}

		try {
			return Serializables.readObject(client.getData().forPath(path));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void persist(ZkPath path) {
		if (path == null) {
			return;
		}
		persist(path.getPath(), path.getData());
	}

	@Override
	public void persist(String path, byte[] data) {
		if (path == null) {
			return;
		}

		try {
			if (!this.exists(path))
				mkdirs(path);
			client.setData().forPath(path, data);
		} catch (Exception e) {
		}
	}

	@Override
	public void persist(String path, String json) {
		this.persist(path, json.getBytes());
	}

	@Override
	public void close() {
		if (client != null) {
			client.close();
		}
	}

}
