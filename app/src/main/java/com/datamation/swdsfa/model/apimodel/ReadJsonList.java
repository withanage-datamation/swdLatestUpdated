package com.datamation.swdsfa.model.apimodel;

import com.datamation.swdsfa.model.Bank;
import com.datamation.swdsfa.model.CompanyBranch;
import com.datamation.swdsfa.model.CompanySetting;
import com.datamation.swdsfa.model.Control;
import com.datamation.swdsfa.model.DbNames;
import com.datamation.swdsfa.model.Debtor;
import com.datamation.swdsfa.model.Discdeb;
import com.datamation.swdsfa.model.Discdet;
import com.datamation.swdsfa.model.Disched;
import com.datamation.swdsfa.model.Discslab;
import com.datamation.swdsfa.model.Expense;
import com.datamation.swdsfa.model.FInvhedL3;
import com.datamation.swdsfa.model.FItenrDet;
import com.datamation.swdsfa.model.FItenrHed;
import com.datamation.swdsfa.model.FddbNote;
import com.datamation.swdsfa.model.FinvDetL3;
import com.datamation.swdsfa.model.FreeDeb;
import com.datamation.swdsfa.model.FreeDet;
import com.datamation.swdsfa.model.FreeHed;
import com.datamation.swdsfa.model.FreeItem;
import com.datamation.swdsfa.model.FreeMslab;
import com.datamation.swdsfa.model.FreeSlab;
import com.datamation.swdsfa.model.Item;
import com.datamation.swdsfa.model.ItemLoc;
import com.datamation.swdsfa.model.ItemPri;
import com.datamation.swdsfa.model.Locations;
import com.datamation.swdsfa.model.NearDebtor;
import com.datamation.swdsfa.model.Reason;
import com.datamation.swdsfa.model.Route;
import com.datamation.swdsfa.model.RouteDet;
import com.datamation.swdsfa.model.SalRep;
import com.datamation.swdsfa.model.Tax;
import com.datamation.swdsfa.model.TaxDet;
import com.datamation.swdsfa.model.TaxHed;
import com.datamation.swdsfa.model.Town;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReadJsonList {

    @SerializedName("GetdatabaseNamesResult")//01
    private List<DbNames> dbResult = null;
    @SerializedName("fSalRepResult")//01
    private List<SalRep> salRepResult = null;
    @SerializedName("fControlResult")//02
    private List<Control> controlResult = null;
    @SerializedName("fItemLocResult")//03
    private List<ItemLoc> itemLocResult = null;
    @SerializedName("fItemPriResult")//05
    private List<ItemPri> itemPriResult = null ;
    @SerializedName("fItemsResult")//06
    private List<Item> itemsResult = null ;
    @SerializedName("fLocationsResult")//07
    private List<Locations> locationsResult = null ;
    @SerializedName("fTaxResult")//08
    private List<Tax> taxResult = null ;
    @SerializedName("fTaxHedResult")//09
    private List<TaxHed> taxHedResult = null ;
    @SerializedName("fTaxDetResult")//10
    private List<TaxDet> taxDetResult = null ;
    @SerializedName("FnearDebtorResult")//11
    private List<NearDebtor> nearDebtorResult = null ;
    @SerializedName("FCompanyBranchResult")//12
    private List<CompanyBranch> companyBranchResult = null ;
    @SerializedName("fCompanySettingResult")//13
    private List<CompanySetting> companySettingResult = null ;
    @SerializedName("fReasonResult")//14
    private List<Reason> reasonResult = null ;
    @SerializedName("fExpenseResult")//15
    private List<Expense> expenseResult = null ;
    @SerializedName("FfreeslabResult")//16
    private List<FreeSlab> freeSlabResult = null ;
    @SerializedName("FfreedetResult")//17
    private List<FreeDet> freeDetResult = null ;
    @SerializedName("FfreedebResult")//18
    private List<FreeDeb> freeDebResult = null ;
    @SerializedName("fFreeItemResult")//19
    private List<FreeItem> freeItemResult = null ;
    @SerializedName("FfreehedResult")//20
    private List<FreeHed> freeHedResult = null ;
    @SerializedName("fFreeMslabResult")//21
    private List<FreeMslab> freeMslabResult = null ;
    @SerializedName("fbankResult")//22
    private List<Bank> bankResult = null ;
    @SerializedName("FdiscdetResult")//23
    private List<Discdet> discDetResult = null ;
    @SerializedName("FdiscslabResult")//24
    private List<Discslab> discSlabResult = null ;
    @SerializedName("FdiscdebResult")//25
    private List<Discdeb> discDebResult = null ;
    @SerializedName("FDischedResult")//26
    private List<Disched> discHedResult = null ;
    @SerializedName("fTownResult")//27
    private List<Town> townResult = null ;
    @SerializedName("fRouteResult")//28
    private List<Route> routeResult = null ;
    @SerializedName("fRouteDetResult")//29
    private List<RouteDet> routeDetResult = null ;
    @SerializedName("fItenrHedResult")//30
    private List<FItenrHed> itenrHedResult = null ;
    @SerializedName("fItenrDetResult")//31
    private List<FItenrDet> itenrDetResult = null ;
    @SerializedName("RepLastThreeInvDetResult")//32
    private List<FinvDetL3> lastThreeInvDetResult = null ;
    @SerializedName("RepLastThreeInvHedResult")//33
    private List<FInvhedL3> lastThreeInvHedResult = null ;
    @SerializedName("fDdbNoteWithConditionResult")//34
    private List<FddbNote> outstandingResult = null ;
    @SerializedName("FdebtorResult")//35
    private List<Debtor> debtorResult = null ;

    public List<DbNames> getDbResult() {
        return dbResult;
    }

    public List<SalRep> getSalRepResult() {
        return salRepResult;
    }

    public List<Control> getControlResult() {
        return controlResult;
    }

    public List<ItemLoc> getItemLocResult() {
        return itemLocResult;
    }

    public List<ItemPri> getItemPriResult() {
        return itemPriResult;
    }

    public List<Item> getItemsResult() {
        return itemsResult;
    }

    public List<Locations> getLocationsResult() {
        return locationsResult;
    }

    public List<Tax> getTaxResult() {
        return taxResult;
    }

    public List<TaxHed> getTaxHedResult() {
        return taxHedResult;
    }

    public List<TaxDet> getTaxDetResult() {
        return taxDetResult;
    }

    public List<NearDebtor> getNearDebtorResult() {
        return nearDebtorResult;
    }

    public List<CompanyBranch> getCompanyBranchResult() {
        return companyBranchResult;
    }

    public List<CompanySetting> getCompanySettingResult() {
        return companySettingResult;
    }

    public List<Reason> getReasonResult() {
        return reasonResult;
    }

    public List<Expense> getExpenseResult() {
        return expenseResult;
    }

    public List<FreeSlab> getFreeSlabResult() {
        return freeSlabResult;
    }

    public List<FreeDet> getFreeDetResult() {
        return freeDetResult;
    }

    public List<FreeDeb> getFreeDebResult() {
        return freeDebResult;
    }

    public List<FreeItem> getFreeItemResult() {
        return freeItemResult;
    }

    public List<FreeHed> getFreeHedResult() {
        return freeHedResult;
    }

    public List<FreeMslab> getFreeMslabResult() {
        return freeMslabResult;
    }

    public List<Bank> getBankResult() {
        return bankResult;
    }

    public List<Discdet> getDiscDetResult() {
        return discDetResult;
    }

    public List<Discslab> getDiscSlabResult() {
        return discSlabResult;
    }

    public List<Discdeb> getDiscDebResult() {
        return discDebResult;
    }

    public List<Disched> getDiscHedResult() {
        return discHedResult;
    }

    public List<Town> getTownResult() {
        return townResult;
    }

    public List<Route> getRouteResult() {
        return routeResult;
    }

    public List<RouteDet> getRouteDetResult() {
        return routeDetResult;
    }

    public List<FItenrHed> getItenrHedResult() {
        return itenrHedResult;
    }

    public List<FItenrDet> getItenrDetResult() {
        return itenrDetResult;
    }

    public List<FinvDetL3> getLastThreeInvDetResult() {
        return lastThreeInvDetResult;
    }

    public List<FInvhedL3> getLastThreeInvHedResult() {
        return lastThreeInvHedResult;
    }

    public List<FddbNote> getOutstandingResult() {
        return outstandingResult;
    }

    public List<Debtor> getDebtorResult() {
        return debtorResult;
    }
}

