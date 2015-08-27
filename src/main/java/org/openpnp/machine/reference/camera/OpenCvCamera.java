/*
 	Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 	
 	This file is part of OpenPnP.
 	
	OpenPnP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenPnP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenPnP.  If not, see <http://www.gnu.org/licenses/>.
 	
 	For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.machine.reference.camera;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Vector;

import javax.swing.Action;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.openpnp.CameraListener;
import org.openpnp.gui.support.PropertySheetWizardAdapter;
import org.openpnp.gui.support.Wizard;
import org.openpnp.gui.wizards.CameraConfigurationWizard;
import org.openpnp.machine.reference.ReferenceCamera;
import org.openpnp.machine.reference.camera.wizards.OpenCvCameraConfigurationWizard;
import org.openpnp.spi.PropertySheetHolder;
import org.openpnp.util.OpenCvUtils;
import org.simpleframework.xml.Attribute;

/**
 * A Camera implementation based on the OpenCV FrameGrabbers.
 */
public class OpenCvCamera extends ReferenceCamera implements Runnable {
    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }    
    
	@Attribute(name="deviceIndex", required=true)
	private int deviceIndex = 0;
	
	@Attribute(required=false)
	private boolean showHistogram = false;
	
	private VideoCapture fg = new VideoCapture();
	private Thread thread;
	
	public OpenCvCamera() {
	}
	
	@Override
	public synchronized BufferedImage capture() {
	    if (thread == null) {
	        setDeviceIndex(deviceIndex);
	    }
		try {
		    Mat mat = new Mat();
		    if (!fg.read(mat)) {
		        return null;
		    }
		    
		    BufferedImage img = OpenCvUtils.toBufferedImage(mat);
            img = applyRotation(img);

		    if (showHistogram) {
	            addHistogram(mat, img);
		    }
            
		    mat.release();
		    return img;
			//return applyRotation(OpenCvUtils.toBufferedImage(mat));
		}
		catch (Exception e) {
		    e.printStackTrace();
			return null;
		}
	}
	
	// TODO: Move this into CameraView, or a layer inbetween, so it works with
	// any type of camera.
	private void addHistogram(Mat src, BufferedImage dest) {
        Graphics g = dest.getGraphics();
        // TODO: Probably need to ensure it's BGR
        Color[] colors = new Color[] { Color.blue, Color.green, Color.red };
        Vector<Mat> hists = new Vector<>();
        Core.split(src, hists);
        for (int j = 0; j < hists.size(); j++) {
            Mat hist = hists.get(j);
            Imgproc.calcHist(
                    Collections.singletonList(src), 
                    new MatOfInt(j), 
                    new Mat(), 
                    hist, 
                    new MatOfInt(255), 
                    new MatOfFloat(0, 256));
            Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);
            double w = dest.getWidth() / 255;
            for (int i = 1; i < 255; i++) {
                g.setColor(colors[j]);
                g.drawLine(
                        (int) ((i - 1) * w), 
                        (int) (hist.get(i - 1, 0)[0] * dest.getHeight()), 
                        (int) (i * w), 
                        (int) (hist.get(i, 0)[0] * dest.getHeight()) 
                        );
            }
        }
        g.dispose();
	}
	
	@Override
    public synchronized void startContinuousCapture(CameraListener listener, int maximumFps) {
	    if (thread == null) {
	        setDeviceIndex(deviceIndex);
	    }
        super.startContinuousCapture(listener, maximumFps);
    }

    public void run() {
		while (!Thread.interrupted()) {
			try {
				BufferedImage image = capture();
				if (image != null) {
					broadcastCapture(image);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000 / 24);
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public int getDeviceIndex() {
		return deviceIndex;
	}

	public synchronized void setDeviceIndex(int deviceIndex) {
		this.deviceIndex = deviceIndex;
		if (thread != null) {
			thread.interrupt();
			try {
				thread.join();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			thread = null;
		}
		try {
		    fg.open(deviceIndex);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public Wizard getConfigurationWizard() {
		return new OpenCvCameraConfigurationWizard(this);
	}
	
    
    @Override
    public String getPropertySheetHolderTitle() {
        return getClass().getSimpleName() + " " + getName();
    }

    @Override
    public PropertySheetHolder[] getChildPropertySheetHolders() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PropertySheet[] getPropertySheets() {
        return new PropertySheet[] {
                new PropertySheetWizardAdapter(new CameraConfigurationWizard(this)),
                new PropertySheetWizardAdapter(getConfigurationWizard())
        };
    }
    
    @Override
    public Action[] getPropertySheetHolderActions() {
        // TODO Auto-generated method stub
        return null;
    }
}
