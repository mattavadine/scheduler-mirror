package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.ui.HTML;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleItemHTML extends HTML
{

 ScheduleItemGWT scheduleItem;

 ScheduleItemHTML(ScheduleItemGWT schdItem)
 {
  super();
  scheduleItem = schdItem;
  setHTML(scheduleItem.getSchdItemText());
  setStyleName("scheduleItemHTML");
 }

 public ScheduleItemGWT getScheduleItem()
 {
  return scheduleItem;
 }
}