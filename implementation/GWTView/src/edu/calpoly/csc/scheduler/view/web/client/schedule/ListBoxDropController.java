package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

/**
 * DropController for {@link DualListExample}.
 */
class ListBoxDropController extends AbstractDropController
{

 private MouseListBox mouseListBox;
 private ScheduleViewWidget schedule;

 ListBoxDropController(MouseListBox mouseListBox, ScheduleViewWidget schedule)
 {
  super(mouseListBox);
  this.mouseListBox = mouseListBox;
  this.schedule = schedule;
 }

 @Override
 public void onDrop(DragContext context)
 {
  MouseListBox from;
  CourseGWT course;
  int itemIndex, sectionsIncluded;

  if (context.draggable instanceof ScheduleItemHTML)
  {
   schedule.removeItem(((ScheduleItemHTML)context.draggable).getScheduleItem());
   
   if(!mouseListBox.isAvailableBox())
   {
    course = new CourseGWT(((ScheduleItemHTML)context.draggable).getScheduleItem().getCourse());
    itemIndex = mouseListBox.contains(new CourseListItem(course, true));
    sectionsIncluded = mouseListBox.getSectionsInBox(course);
    if (itemIndex >= 0)
    {
     course.setNumSections(sectionsIncluded + 1);
     mouseListBox.setWidget(itemIndex, new CourseListItem(course, true));
    } else
    {
     course.setNumSections(1);
     mouseListBox.add(new CourseListItem(course, true));
    }
   }
  } else
  {
   from = (MouseListBox) context.draggable.getParent().getParent();
   for (Widget widget : context.selectedWidgets)
   {
    if (widget.getParent().getParent() == from)
    {
     if (mouseListBox.isAvailableBox())
     {
      from.remove(widget);
     }
     else
     {
      itemIndex = mouseListBox.contains((CourseListItem) widget);
      course = new CourseGWT(((CourseListItem) widget).getCourse());
      sectionsIncluded = mouseListBox.getSectionsInBox(course);

      if (course.getNumSections() > sectionsIncluded
        + schedule.getSectionsOnSchedule(course))
      {
       if (itemIndex >= 0)
       {
        course.setNumSections(sectionsIncluded + 1);
        mouseListBox.setWidget(itemIndex, new CourseListItem(course, true));
       } else
       {
        course.setNumSections(1);
        mouseListBox.add(new CourseListItem(course, true));
       }
      } else
      {
       Window.alert("No more sections to schedule");
      }
     }
    }
   }
  }
  super.onDrop(context);
 }

 @Override
 public void onPreviewDrop(DragContext context) throws VetoDragException
 {
  if(context.draggable instanceof CourseListItem)
  {
   MouseListBox from = (MouseListBox) context.draggable.getParent().getParent();
   if (from == mouseListBox)
   {
    throw new VetoDragException();
   }
  }
  super.onPreviewDrop(context);
 }
}
