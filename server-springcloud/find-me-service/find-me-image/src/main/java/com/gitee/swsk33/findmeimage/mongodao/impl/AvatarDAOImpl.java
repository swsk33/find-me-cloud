package com.gitee.swsk33.findmeimage.mongodao.impl;

import com.gitee.swsk33.findmeimage.mongodao.AvatarDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class AvatarDAOImpl implements AvatarDAO {

	@Autowired
	private GridFsOperations gridFsOperations;

	@Override
	public void add(InputStream inputStream, String id) {
		gridFsOperations.store(inputStream, id);
	}

	@Override
	public void delete(String id) {
		gridFsOperations.delete(Query.query(GridFsCriteria.whereFilename().is(id)));
	}

	@Override
	public byte[] get(String id) throws IOException {
		GridFsResource[] files = gridFsOperations.getResources(id);
		if (files.length == 0) {
			return null;
		}
		return files[0].getContentAsByteArray();
	}

}