package synergyviewmvc.timebar.render;

import java.util.ArrayList;

import de.jaret.util.date.iterator.DateIterator;
import de.jaret.util.date.iterator.DayIterator;
import de.jaret.util.date.iterator.HourIterator;
import de.jaret.util.date.iterator.MillisecondIterator;
import de.jaret.util.date.iterator.MinuteIterator;
import de.jaret.util.date.iterator.MonthIterator;
import de.jaret.util.date.iterator.SecondIterator;
import de.jaret.util.date.iterator.WeekIterator;
import de.jaret.util.date.iterator.YearIterator;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTimeScaleRenderer;

public class TimeBarTimeScaleRender extends DefaultTimeScaleRenderer {

	 protected static final int PREFERREDHEIGHT = 30;
	    
	    @Override
	    public int getHeight() {
	        if (_printer == null) {
	            return PREFERREDHEIGHT;
	        } else {
	            return scaleY(PREFERREDHEIGHT);
	        }
	    }
	
	protected void initIterators() {
        _iterators = new ArrayList<DateIterator>();
        _formats = new ArrayList<DateIterator.Format>();

        DateIterator iterator = new MillisecondIterator(1);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new MillisecondIterator(10);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new MillisecondIterator(100);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new MillisecondIterator(500);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new SecondIterator(1);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new SecondIterator(5);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        
        iterator = new SecondIterator(10);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        
        // TODO Why commented
        //iterator = new SecondIterator(30);
        //_iterators.add(iterator);
        //_formats.add(DateIterator.Format.LONG);

        iterator = new MinuteIterator(1);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        
        // TODO Why commented
        //iterator = new MinuteIterator(5);
        //_iterators.add(iterator);
        //_formats.add(DateIterator.Format.LONG);

        iterator = new MinuteIterator(10);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new DayIterator(1));

        iterator = new MinuteIterator(30);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new DayIterator(1));

        iterator = new HourIterator(3);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new DayIterator(1));

        iterator = new HourIterator(12);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new DayIterator(1));

        iterator = new DayIterator();
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new WeekIterator();
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new MonthIterator());

        iterator = new MonthIterator();
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new YearIterator());

        iterator = new YearIterator();
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
    }
	
}
