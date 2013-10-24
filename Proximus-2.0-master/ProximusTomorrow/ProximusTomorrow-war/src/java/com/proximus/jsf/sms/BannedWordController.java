/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.sms.BannedWord;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.BannedWordDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.BannedWordManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "bannedWordController")
@SessionScoped
/**
 * @author eric
 */
public class BannedWordController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    BannedWordManagerLocal wordFacade;
    @EJB
    CompanyManagerLocal companyFacade;
    private BannedWord newWord;
    private BannedWord selectedWord;
    private BannedWordDataModel bwDataModel;
    List<BannedWord> filteredBannedWord;
    private ResourceBundle message;

    public BannedWordController() {
        message = this.getHttpSession().getMessages();
    }

    public BannedWord getSelectedWord() {
        if (this.selectedWord == null) {
            this.selectedWord = new BannedWord();
        }
        return selectedWord;
    }

    public void setSelectedWord(BannedWord selectedWord) {
        this.selectedWord = selectedWord;
    }

    public BannedWord getNewWord() {
        if (newWord == null) {
            newWord = new BannedWord();
        }
        return newWord;
    }

    public void setNewWord(BannedWord newWord) {
        this.newWord = newWord;
    }

    public BannedWordDataModel getBwDataModel() {
        if (this.bwDataModel == null) {
            populateBannedWordModel();

        }
        return bwDataModel;
    }

    public void setBwDataModel(BannedWordDataModel bwDataModel) {
        this.bwDataModel = bwDataModel;
    }

    public List<BannedWord> getFilteredBannedWord() {
        return filteredBannedWord;
    }

    public void setFilteredBannedWord(List<BannedWord> filteredBannedWord) {
        this.filteredBannedWord = filteredBannedWord;
    }

    public void createNewWord(BannedWord bannedWord) {
        if (bannedWord == null || bannedWord.getWord() == null || bannedWord.getWord().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("bannedWordNameError"));
            bannedWord = new BannedWord();
            return;
        }
        List<String> bannedWords = new ArrayList<String>();
        if (bwDataModel.getBannedWordData() != null && !bwDataModel.getBannedWordData().isEmpty()) {
            for (BannedWord b : bwDataModel.getBannedWordData()) {
                bannedWords.add(b.getWord().toLowerCase());
            }


            if (bannedWords.contains(bannedWord.getWord().toLowerCase())) {
                JsfUtil.addErrorMessage(message.getString("bannedWordDuplicateError"));
                return;
            }

        }

        bannedWord.setCompany(companyFacade.find(this.getCompanyIdFromSession()));
        wordFacade.create(bannedWord);
        JsfUtil.addSuccessMessage(message.getString("bannedWordCreated"));
        prepareList();
    }

    public String prepareList() {
        this.newWord = null;
        this.selectedWord = null;
        this.bwDataModel = null;
        return "/banned-word/List?faces-redirect=true";
    }

    private void populateBannedWordModel() {
        List<BannedWord> words = wordFacade.findAllByCompany(companyFacade.getCompanybyId(this.getCompanyIdFromSession()));
        this.bwDataModel = new BannedWordDataModel(words);
        filteredBannedWord = new ArrayList<BannedWord>(words);
    }

    public boolean editBannedWord() {
        try {
            if (selectedWord.getWord() == null || selectedWord.getWord().isEmpty()) {
                JsfUtil.addErrorMessage(message.getString("bannedWordNameError"));
                prepareList();
                return false;
            }

            List<String> bannedWords = new ArrayList<String>();
            if (bwDataModel.getBannedWordData() != null && !bwDataModel.getBannedWordData().isEmpty()) {
                for (BannedWord b : bwDataModel.getBannedWordData()) {
                    bannedWords.add(b.getWord().toLowerCase());
                }


                if (bannedWords.contains(selectedWord.getWord().toLowerCase())) {
                    JsfUtil.addErrorMessage(message.getString("bannedWordDuplicateError"));
                    return false;
                }

            }

            wordFacade.update(selectedWord);
            prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("bannedWordPersistError"));
            prepareList();
            return false;
        }
        return true;
    }

    public void deleteBannedWord() {
        if (selectedWord == null) {
            return;
        }
        try {
            wordFacade.delete(selectedWord);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("bannedWordDeleteError") + ": " + selectedWord.getWord());
        }
        prepareList();
    }
}
