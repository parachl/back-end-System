package com.thailife.tax.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.thailife.tax.base.ServiceBase;
import com.thailife.tax.entity.User;
import com.thailife.tax.object.SecuityUserObj;
import com.thailife.tax.object.UserObj;
import com.thailife.tax.object.criteria.GroupMenuObjC;
import com.thailife.tax.repository.UserRepository;
import com.thailife.tax.utils.JwtUtil;
import com.thailife.tax.utils.SecurityUtils;

@Service
public class LoginService extends ServiceBase {

	@Autowired
	private MenuService menuService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MenuManageService menuManageService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	public GroupMenuObjC listMenu(String userId) throws Exception {

		GroupMenuObjC groupMenuObjC = new GroupMenuObjC();
		groupMenuObjC = menuManageService.permissionMenu(menuService.menuFromUserPermission(userId));

		return groupMenuObjC;
	}

	public SecuityUserObj verifyLogin(UserObj userObj) throws Exception {
		SecuityUserObj secuityUserObj = new SecuityUserObj();
		User user = new User();
		try {
			user = userRepository.findUserName(userObj.getUserName());
			if (null != user.getId()) {

				secuityUserObj.setToken(setToken(userObj));
				secuityUserObj.setTypeUser(user.getTypeUser());
				secuityUserObj.setIsLogin(true);
				SecurityUtils.terminateSession();
				SecurityUtils.setSecurityInfo(secuityUserObj);
			} else {
				secuityUserObj.setIsLogin(false);
			}

		} catch (Exception e) {
			logger.error("Error ", e);
			throw e;
		}
		return secuityUserObj;
	}

	public String setToken(UserObj userObj) {
		String jwt = null;
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userObj.getUserName(), userObj.getPassword()));
			 jwt = jwtUtil.generateToken(userObj.getUserName());
		} catch (BadCredentialsException e) {
			// TODO: handle exception
		}
		return jwt;
	}
}
