/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class Hologram {

    private int hologramX;
    private int hologramY;
    private double hologramSize;
    private int hologramRotation;

    public Hologram(int hologramX, int hologramY, double hologramSize, int hologramRotation) {
        this.hologramX = hologramX;
        this.hologramY = hologramY;
        this.hologramSize = hologramSize;
        this.hologramRotation = hologramRotation;
    }

    public int getHologramX() {
        return hologramX;
    }

    public void setHologramX(int hologramX) {
        this.hologramX = hologramX;
    }

    public int getHologramY() {
        return hologramY;
    }

    public void setHologramY(int hologramY) {
        this.hologramY = hologramY;
    }

    public double getHologramSize() {
        return hologramSize;
    }

    public void setHologramSize(double hologramSize) {
        this.hologramSize = hologramSize;
    }

    public int getHologramRotation() {
        return hologramRotation;
    }

    public void setHologramRotation(int hologramRotation) {
        this.hologramRotation = hologramRotation;
    }
}
