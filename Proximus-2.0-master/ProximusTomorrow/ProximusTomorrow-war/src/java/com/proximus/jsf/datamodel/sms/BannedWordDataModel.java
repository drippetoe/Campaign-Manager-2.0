package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.BannedWord;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author eric
 */
public class BannedWordDataModel extends ListDataModel<BannedWord> implements SelectableDataModel<BannedWord> {


    public BannedWordDataModel() {
    }

    public List<BannedWord> getBannedWordData() {
        return (List<BannedWord>) this.getWrappedData();
    }

    public BannedWordDataModel(List<BannedWord> bannedWordData) {
        super(bannedWordData);
    }

    @Override
    public Object getRowKey(BannedWord b) {
        return b.getWord();
    }

    @Override
    public BannedWord getRowData(String rowKey) {
        List<BannedWord> bannedWords = (List<BannedWord>) getWrappedData();
        for (BannedWord b : bannedWords) {
            if (b.getWord().equals(rowKey)) {
                return b;
            }
        }
        return null;
    }
}
