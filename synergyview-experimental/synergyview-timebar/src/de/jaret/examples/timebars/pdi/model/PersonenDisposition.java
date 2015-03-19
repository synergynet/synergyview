/*
 *  File: PersonenDisposition.java 
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
package de.jaret.examples.timebars.pdi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.AbstractTimeBarRowModel;

/**
 * @author Peter Kliem
 * @version $Id: PersonenDisposition.java 259 2007-02-16 13:54:00Z olk $
 */
public class PersonenDisposition extends AbstractTimeBarRowModel {
    protected PdiCalendar _kalender;
    protected Person _person;
    protected List _verplanungen = new ArrayList();
    protected List _rfgRules = new ArrayList();

    public PersonenDisposition(PdiCalendar kalender, Person person) {
        _kalender = kalender;
        _person = person;
        setRowHeader(_person);
        _rfgRules.add(new Max10hin24h());
        _rfgRules.add(new Max60hin7Tagen());
    }

    /**
     * {@inheritDoc}
     */
    public List getIntervals() {
        return _verplanungen;
    }

    public void addDienst(Duty dienst) {
        Assignment v = new Assignment(dienst.getTag(), dienst, this);
        dienst.setAssignedTo(_person);
        _verplanungen.add(v);
        Collections.sort(_verplanungen, new Comparator() {
            public int compare(Object arg0, Object arg1) {
                Interval i1 = (Interval) arg0;
                Interval i2 = (Interval) arg1;

                return i1.getBegin().compareTo(i2.getBegin());
            }
        });
        check(dienst);
        fireElementAdded(v);
    }

    public void remVerplanung(Assignment verplanung) {
        _verplanungen.remove(verplanung);
        if (verplanung.getTaetigkeit() instanceof Duty) {
            ((Duty) verplanung.getTaetigkeit()).setAssignedTo(null);
            verplanung.getTaetigkeit().clearProbleme();
        }
        checkRow();
        fireElementRemoved(verplanung);
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate getMinDate() {
        return _kalender.getMinDate();
    }

    public JaretDate getMaxDate() {
        return _kalender.getMaxDate();
    }

    /**
     * @param dd
     * @return
     */
    public boolean allowed(Duty dd) {
        Iterator it = _verplanungen.iterator();
        while (it.hasNext()) {
            Assignment verp = (Assignment) it.next();
            if (verp.intersects(dd)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return "PDispo:" + _person.getName();
    }

    /**
     * @return Returns the kalender.
     */
    public PdiCalendar getKalender() {
        return _kalender;
    }

    /**
     * @return Returns the person.
     */
    public Person getPerson() {
        return _person;
    }

    public void check(Taetigkeit taetigkeit) {
        taetigkeit.clearProbleme();
        Iterator it = _rfgRules.iterator();
        while (it.hasNext()) {
            RfgRule rule = (RfgRule) it.next();
            rule.check(this, taetigkeit);
        }
    }

    public void checkRow() {
        Iterator it = getIntervals().iterator();
        while (it.hasNext()) {
            Taetigkeit taetigkeit = ((Assignment) it.next()).getTaetigkeit();
            check(taetigkeit);
        }
    }

    public Taetigkeit getTaetigkeit(int idx) {
        return ((Assignment) getIntervals().get(idx)).getTaetigkeit();
    }

    /**
     * @param taetigkeit
     * @return
     */
    public int indexOf(Taetigkeit taetigkeit) {
        for (int i = 0; i < getIntervals().size(); i++) {
            if (taetigkeit == getTaetigkeit(i)) {
                return i;
            }
        }
        throw new RuntimeException("not found");
    }

    class Max10hin24h implements RfgRule {

        /**
         * {@inheritDoc}
         */
        public boolean check(PersonenDisposition pdispo, Taetigkeit taetigkeit) {
            // nach vorne checken bis Anfang > 24h von Anfang der Tätigkeit weg
            int idx = pdispo.indexOf(taetigkeit);
            int i = idx - 1;
            while (i >= 0 && taetigkeit.getBegin().diffSeconds(pdispo.getTaetigkeit(i).getBegin()) < 24 * 60 * 60) {
                checknachHinten(pdispo, pdispo.getTaetigkeit(i));
                i--;
            }
            // und selbst nach hinten checken
            checknachHinten(pdispo, taetigkeit);
            return true;
        }

        /**
         * @param pdispo
         * @param taetigkeit
         */
        private void checknachHinten(PersonenDisposition pdispo, Taetigkeit taetigkeit) {
            // System.out.println("Check "+((Dienst)taetigkeit).getDienstNr());
            int az = taetigkeit.getBezahlteZeitSeconds();
            int idx = pdispo.indexOf(taetigkeit) + 1;
            // System.out.println("idx "+idx+" diff
            // "+pdispo.getTaetigkeit(idx).getBegin().diffSeconds(taetigkeit.getBegin()));
            while (idx < pdispo.getIntervals().size()
                    && pdispo.getTaetigkeit(idx).getBegin().diffSeconds(taetigkeit.getBegin()) < 24 * 60 * 60) {
                // System.out.println("index "+idx+ "az "+az);
                int alteAz = az;
                az += pdispo.getTaetigkeit(idx).getBezahlteZeitSeconds();
                if (az > 10 * 60 * 60) {
                    int diff = 10 * 60 * 60 - alteAz;
                    JaretDate vorbei = pdispo.getTaetigkeit(idx).getBegin().copy();
                    vorbei.advanceSeconds(diff);
                    taetigkeit.addProblem("<b>" + getName() + "</b>: Überschritten etwa bei "
                            + vorbei.toDisplayString() + "<br/");
                    break;
                }
                idx++;
            }
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return "Max. 10h in 24h";
        }

        /**
         * {@inheritDoc}
         */
        public String getDescription() {
            return "Nach Arbeitszeitgesetz max. 10 Stunden schutzrechtliche Arbeitszeit innerhalb 24h vom 1. Dienstbeginn zählend";
        }

    }

    class Max60hin7Tagen implements RfgRule {

        /**
         * {@inheritDoc}
         */
        public boolean check(PersonenDisposition pdispo, Taetigkeit taetigkeit) {
            // nach vorne checken bis Anfang > 7*24h von Anfang der Tätigkeit
            // weg
            int idx = pdispo.indexOf(taetigkeit);
            int i = idx - 1;
            while (i >= 0 && taetigkeit.getBegin().diffSeconds(pdispo.getTaetigkeit(i).getBegin()) < 7 * 24 * 60 * 60) {
                checknachHinten(pdispo, pdispo.getTaetigkeit(i));
                i--;
            }
            // und selbst nach hinten checken
            checknachHinten(pdispo, taetigkeit);
            return true;
        }

        private void checknachHinten(PersonenDisposition pdispo, Taetigkeit taetigkeit) {
            int az = taetigkeit.getBezahlteZeitSeconds();
            int idx = pdispo.indexOf(taetigkeit) + 1;
            // System.out.println("idx "+idx+" diff
            // "+pdispo.getTaetigkeit(idx).getBegin().diffSeconds(taetigkeit.getBegin()));
            while (idx < pdispo.getIntervals().size()
                    && pdispo.getTaetigkeit(idx).getBegin().diffSeconds(taetigkeit.getBegin()) < 7 * 24 * 60 * 60) {
                int alteAz = az;
                az += pdispo.getTaetigkeit(idx).getBezahlteZeitSeconds();
                if (az > 60 * 60 * 60) {
                    int diff = 60 * 60 * 60 - alteAz;
                    JaretDate vorbei = pdispo.getTaetigkeit(idx).getBegin().copy();
                    vorbei.advanceSeconds(diff);
                    taetigkeit.addProblem("<b>" + getName() + "</b>: Überschritten etwa bei "
                            + vorbei.toDisplayString() + "<br/");
                    break;
                }
                idx++;
            }
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return "Max 60h in 7 Tagen";
        }

        /**
         * {@inheritDoc}
         */
        public String getDescription() {
            return "55h Arbeitszeit (bezahlte Zeit!) innerhalb von 7 aufeinanderfolgenden Tagen (laufend, nicht wochenbezogen) dürfen bei der Planung nicht überschritten werden. Bei Nichteinhaltung Anzeige, ab welchem Tag bzw. Woche. In der Disposition sind 60h möglich";
        }

    }

}
