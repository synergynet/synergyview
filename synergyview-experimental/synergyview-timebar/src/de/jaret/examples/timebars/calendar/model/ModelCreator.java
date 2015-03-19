package de.jaret.examples.timebars.calendar.model;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.TimeBarModel;

public class ModelCreator {
    public static TimeBarModel createCalendarModel() {
        CalendarModel model = new CalendarModel();

        Day secondOfMay;
        
        model.createMonth(4, 2007);
        model.createMonth(5, 2007);
        model.createMonth(6, 2007);
        model.createMonth(7, 2007);
        
        JaretDate date = new JaretDate(30, 4, 2007, 0, 0, 0); 
        // 30.4.
        Day day = model.getDay(date);
        Appointment a = new Appointment(date, 12, 0, 2.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 15, 0, 1.5, "Second appointment");
        day.addInterval(a);
        
        // 1.5.
        date = date.copy().advanceDays(1);
        day = model.getDay(date);
        a = new Appointment(date, 8, 0, 2.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 17, 0, 1.5, "Second appointment");
        day.addInterval(a);
        
        // 2.5.
        date = date.copy().advanceDays(1);
        day = model.getDay(date);
        a = new Appointment(date, 8, 0, 2.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 17, 0, 1.5, "Second appointment");
        day.addInterval(a);
        secondOfMay = day;
        
        // 3.5.
        date = date.copy().advanceDays(1);
        day = model.getDay(date);
        a = new Appointment(date, 7, 0, 1.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 9, 0, 2, "Second appointment");
        day.addInterval(a);

        // 4.5.
        date = date.copy().advanceDays(1);
        day = model.getDay(date);
        a = new Appointment(date, 7, 0, 1.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 9, 0, 2, "Second appointment");
        day.addInterval(a);
        
        // 5.5.
        date = date.copy().advanceDays(1);
        day = model.getDay(date);
        a = new Appointment(date, 7, 0, 1.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 9, 0, 2, "Second appointment");
        day.addInterval(a);
        
        // 6.5.
        date = date.copy().advanceDays(1);
        day = model.getDay(date);
        a = new Appointment(date, 7, 0, 1.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 9, 0, 2, "Second appointment (locked)");
        a.setEditable(false);
        day.addInterval(a);
        a = new Appointment(date, 13, 0, 2, "Recurring appointment");
        a.setRecurring(true);
        day.addInterval(a);
        
        // 7.5.
        date = date.copy().advanceDays(1);
        day = model.getDay(date);
        a = new Appointment(date, 7, 0, 1.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 9, 0, 2, "Second appointment");
        day.addInterval(a);
        a = new Appointment(date, 13, 0, 2, "Recurring appointment (locked)");
        a.setRecurring(true);
        a.setEditable(false);
        day.addInterval(a);
        
        // 8.5.
        date = date.copy().advanceDays(1);
        day = model.getDay(date);
        a = new Appointment(date, 7, 0, 1.0, "First appointment");
        day.addInterval(a);
        a = new Appointment(date, 9, 0, 2, "Second appointment");
        day.addInterval(a);
        
        
        // add amultiday appointment on second of may
        a = new Appointment(secondOfMay.getDayDate(), 9, 0, 48, "Spanning appointment");
        secondOfMay.addInterval(a);

        
        return model;
    }

}
