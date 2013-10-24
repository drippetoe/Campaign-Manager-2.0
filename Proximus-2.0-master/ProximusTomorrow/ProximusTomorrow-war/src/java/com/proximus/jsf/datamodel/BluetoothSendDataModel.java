/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.BluetoothSend;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class BluetoothSendDataModel extends ListDataModel<BluetoothSend> implements SelectableDataModel<BluetoothSend> {

    public BluetoothSendDataModel() {
    }

    public BluetoothSendDataModel(List<BluetoothSend> btSendData) {
        super(btSendData);

    }

    public List<BluetoothSend> getBluetoothSendData() {
        return (List<BluetoothSend>) this.getWrappedData();
    }

    public void setBluetoothSendData(List<BluetoothSend> btSendData) {
        this.setWrappedData(btSendData);
    }

    @Override
    public Object getRowKey(BluetoothSend b) {
        return b.getMacAddress();
    }

    @Override
    public BluetoothSend getRowData(String rowKey) {
        List<BluetoothSend> sentBluetooth = (List<BluetoothSend>) getWrappedData();
        for (BluetoothSend b : sentBluetooth) {
            if (b.getMacAddress().equalsIgnoreCase(rowKey)) {
                return b;
            }
        }
        return null;
    }
}
