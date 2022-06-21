package com.simplecrmapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simplecrmapi.dao.RoleDAO;
import com.simplecrmapi.entity.Role;

@Service(value="roleService")
public class RoleServiceImpl implements RoleService {

	@Autowired
	public RoleDAO roleDAO;
	
	@Override
	public Role findRoleByName(String role) {
		Role found = roleDAO.findRoleByName(role);
		return found;
	}

}
