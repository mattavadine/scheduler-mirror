package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;

public class LoginViewAutomatic extends LoginView {
	final String automaticLoginUsername;
	final int automaticOpenDocumentID;
	
	public LoginViewAutomatic(GreetingServiceAsync service, SimplePanel usernameContainer,
			SimplePanel logoutLinkContainer, MenuBar menuBar, String automaticLoginUsername, int automaticOpenDocumentID) {
		super(service, usernameContainer, logoutLinkContainer, menuBar);
		this.automaticLoginUsername = automaticLoginUsername;
		this.automaticOpenDocumentID = automaticOpenDocumentID;
	}
	
	public LoginViewAutomatic(GreetingServiceAsync service, SimplePanel usernameContainer,
	      SimplePanel logoutLinkContainer, MenuBar menuBar, String automaticLoginUsername) {
	   super(service, usernameContainer, logoutLinkContainer, menuBar);
	   this.automaticLoginUsername = automaticLoginUsername;
	   this.automaticOpenDocumentID = Integer.MIN_VALUE;
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		super.afterPush(frame);
		
		submitLogin(automaticLoginUsername);
	}
	
	@Override
	protected void pushSelectScheduleView(String username) {
		assert(username.equals(automaticLoginUsername));
		
		assert(myFrame.canPopViewsAboveMe());
		myFrame.popFramesAboveMe();
		if(automaticOpenDocumentID == Integer.MIN_VALUE)
		{
		   myFrame.frameViewAndPushAboveMe(new SelectScheduleView(service, menuBar, username));
		}
		else
		{
		   myFrame.frameViewAndPushAboveMe(new SelectScheduleViewAutomatic(service, menuBar, username, automaticOpenDocumentID));
		}
	}
	
}
