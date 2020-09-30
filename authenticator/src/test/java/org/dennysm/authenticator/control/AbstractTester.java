package org.dennysm.authenticator.control;

import java.lang.reflect.Field;

import org.dennysm.microservice.control.TokenService;
import org.dennysm.microservice.control.UserStore;
import org.dennysm.microservice.entity.UserData; 
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTester {
	final String salt = "3atSh!t";
	
	final String password="tDHeJowOzbqjgiZ2RAubAtL+GsyWdvkiFy+3TYFW824=";
	

	@Mock
	UserStore store;
	
	@Mock
	TokenService tokenSvc;
	
	
	UserData activeUser = new UserData(1L, "bolob", "bolobolo", "kZsWb0J+7Y+Jluen39xW4+Rwu7mvotqKbBRbwlbZ+WU=", 1);
	UserData inactiveUser = new UserData(1L, "bolob", "bolobolo", "kZsWb0J+7Y+Jluen39xW4+Rwu7mvotqKbBRbwlbZ+WU=", -1);
	
	Field f1, f2, f3;
	
	abstract public void init();
	
	
}
