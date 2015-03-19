/*
 *  File: ModelCreator.java 
 *  Copyright (c) 2004-2007  Peter Kliem (Peter.Kliem@jaret.de)
 *  A commercial license is available, see http://www.jaret.de.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package de.jaret.examples.timebars.timeline.model;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;

public class ModelCreator {
    private DefaultTimeBarModel _model = new DefaultTimeBarModel();
    /** dateformat for parsing. */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss 'GMT'Z", Locale.US);
    /** dateformat for parsing. */
    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM d yyyy HH:mm:ss 'GMT'Z", Locale.US);

    public ModelCreator(String rscName) {
        fillModel(_model, getClass().getResourceAsStream(rscName));
        System.out.println("Model start "+_model.getMinDate().toDisplayString());
        System.out.println("Model end "+_model.getMaxDate().toDisplayString());
    }

    private void fillModel(DefaultTimeBarModel model, InputStream inStream) {
        DefaultTimeBarRowModel row = new DefaultTimeBarRowModel(new DefaultRowHeader("events"));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(inStream);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Model could not be parsed ... exiting");
            System.exit(1);
        }
        if (document != null) {
            NodeList eventList = document.getElementsByTagName("event");
            for (int i = 0; i < eventList.getLength(); i++) {
                Node node = eventList.item(i);
                TimelineEvent event = createEvent(node);
                if (event != null) {
                    row.addInterval(event);
                } else {
                    System.out.println("Parse error on " + node.getAttributes().getNamedItem("title"));
                }
            }
        }

        _model.addRow(row);
    }

    /**
     * Parse en event node.
     * 
     * @param node the dom node
     * @return timeline event or <code>null</code> if somthing ist not parseble
     */
    private TimelineEvent createEvent(Node node) {
        TimelineEvent event = new TimelineEvent();
        event.setTitle(node.getAttributes().getNamedItem("title").getTextContent());
        event.setContent(node.getTextContent());

        String startStr = node.getAttributes().getNamedItem("start").getTextContent();

        Date date = null;
        try {
            date = dateFormat.parse(startStr);
        } catch (ParseException e) {
            try {
                date = dateFormat2.parse(startStr);
            } catch (ParseException e1) {
                return null;
            }
        }
        event.setBegin(new JaretDate(date));
        event.setEnd(new JaretDate(date));

        if (node.getAttributes().getNamedItem("isDuration") != null) {
            event.setDuration(true);

            String endStr = node.getAttributes().getNamedItem("end").getTextContent();
            date = null;
            try {
                date = dateFormat.parse(endStr);
            } catch (ParseException e) {
                try {
                    date = dateFormat2.parse(endStr);
                } catch (ParseException e1) {
                    return null;
                }
            }
            event.setEnd(new JaretDate(date));
        }

        if (node.getAttributes().getNamedItem("color") != null) {
            event.setColor(node.getAttributes().getNamedItem("color").getTextContent());
        }
        
        return event;
    }

    public TimeBarModel getModel() {
        return _model;
    }

}
