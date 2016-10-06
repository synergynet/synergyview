package synergyviewmvc.timebar.model;

import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;

public class ModelBuilder {
    public static TimeBarModel createModel() {
        
    	DefaultTimeBarModel model = new DefaultTimeBarModel();
    	
        DefaultRowHeader header = new DefaultRowHeader("Media Collection");
        DefaultTimeBarRowModel timeBarRow = new DefaultTimeBarRowModel(header);
        model.addRow(timeBarRow);
        
        header = new DefaultRowHeader("Segments Collection");
        timeBarRow = new DefaultTimeBarRowModel(header);
        model.addRow(timeBarRow);

        return model;
    }

}
