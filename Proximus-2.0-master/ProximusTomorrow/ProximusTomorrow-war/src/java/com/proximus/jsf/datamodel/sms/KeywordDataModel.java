/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.Keyword;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Gilberto Gaxiola
 */
public class KeywordDataModel extends ListDataModel<Keyword> implements SelectableDataModel<Keyword> {

    public KeywordDataModel() {
    }

    public KeywordDataModel(List<Keyword> bannedWordData) {
        super(bannedWordData);
    }

    public List<Keyword> getKeywordData() {
        return (List<Keyword>) this.getWrappedData();
    }

    @Override
    public Object getRowKey(Keyword k) {
        return k.getKeyword();
    }

    @Override
    public Keyword getRowData(String rowKey) {
        List<Keyword> keywords = (List<Keyword>) getWrappedData();
        for (Keyword k : keywords) {
            if (k.getKeyword().equals(rowKey)) {
                return k;
            }
        }
        return null;
    }
}
