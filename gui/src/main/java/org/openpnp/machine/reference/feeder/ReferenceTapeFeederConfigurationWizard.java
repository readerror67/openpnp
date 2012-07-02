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

package org.openpnp.machine.reference.feeder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingListener;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.DoubleConverter;
import org.openpnp.gui.support.JBindings;
import org.openpnp.gui.support.JBindings.WrappedBinding;
import org.openpnp.gui.support.LengthConverter;
import org.openpnp.gui.support.Wizard;
import org.openpnp.gui.support.WizardContainer;
import org.openpnp.model.Location;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

class ReferenceTapeFeederConfigurationWizard extends JPanel implements Wizard {
	private final ReferenceTapeFeeder feeder;
	
	private WizardContainer wizardContainer;

	private JTextField feedStartX;
	private JTextField feedStartY;
	private JTextField feedStartZ;
	private JTextField feedEndX;
	private JTextField feedEndY;
	private JTextField feedEndZ;
	private JTextField feedRate;
	private JButton feedStartAutoFill;
	private JButton feedEndAutoFill;
	private JButton btnSave;

	private List<WrappedBinding> wrappedBindings = new ArrayList<WrappedBinding>();

	public ReferenceTapeFeederConfigurationWizard(ReferenceTapeFeeder referenceTapeFeeder) {
		this.feeder = referenceTapeFeeder;

		setLayout(new BorderLayout());

		JPanel panelFields = new JPanel();
		panelFields.setLayout(new BoxLayout(panelFields, BoxLayout.Y_AXIS));

		panelGeneral = new JPanel();
		panelGeneral.setBorder(new TitledBorder(null, "General Settings",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		panelFields.add(panelGeneral);
		panelGeneral.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblFeedRate = new JLabel("Feed Rate");
		panelGeneral.add(lblFeedRate, "2, 2");

		feedRate = new JTextField();
		panelGeneral.add(feedRate, "4, 2");
		feedRate.setColumns(5);

		lblActuatorId = new JLabel("Actuator Id");
		panelGeneral.add(lblActuatorId, "2, 4");

		textFieldActuatorId = new JTextField();
		panelGeneral.add(textFieldActuatorId, "4, 4");
		textFieldActuatorId.setColumns(5);

		JScrollPane scrollPane = new JScrollPane(panelFields);

		panelLocations = new JPanel();
		panelFields.add(panelLocations);
		panelLocations.setBorder(new TitledBorder(null, "Locations",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLocations.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblX = new JLabel("X");
		panelLocations.add(lblX, "4, 4");

		JLabel lblY = new JLabel("Y");
		panelLocations.add(lblY, "6, 4");

		JLabel lblZ = new JLabel("Z");
		panelLocations.add(lblZ, "8, 4");

		JLabel lblFeedStartLocation = new JLabel("Feed Start Location");
		panelLocations.add(lblFeedStartLocation, "2, 6");

		feedStartX = new JTextField();
		panelLocations.add(feedStartX, "4, 6");
		feedStartX.setColumns(10);

		feedStartY = new JTextField();
		panelLocations.add(feedStartY, "6, 6");
		feedStartY.setColumns(10);

		feedStartZ = new JTextField();
		panelLocations.add(feedStartZ, "8, 6");
		feedStartZ.setColumns(10);

		feedStartAutoFill = new JButton("Set to Current");
		panelLocations.add(feedStartAutoFill, "10, 6");

		JLabel lblFeedEndLocation = new JLabel("Feed End Location");
		panelLocations.add(lblFeedEndLocation, "2, 8");

		feedEndX = new JTextField();
		panelLocations.add(feedEndX, "4, 8");
		feedEndX.setColumns(10);

		feedEndY = new JTextField();
		panelLocations.add(feedEndY, "6, 8");
		feedEndY.setColumns(10);

		feedEndZ = new JTextField();
		panelLocations.add(feedEndZ, "8, 8");
		feedEndZ.setColumns(10);

		feedEndAutoFill = new JButton("Set to Current");
		panelLocations.add(feedEndAutoFill, "10, 8");

		feedEndAutoFill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Location l = wizardContainer.getMachineControlsPanel()
						.getDisplayedLocation();
				l = l.convertToUnits(feeder.getFeedEndLocation().getUnits());
				feedEndX.setText(l.getLengthX().toString());
				feedEndY.setText(l.getLengthY().toString());
				feedEndZ.setText(l.getLengthZ().toString());
			}
		});

		feedStartAutoFill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Location l = wizardContainer.getMachineControlsPanel()
						.getDisplayedLocation();
				l = l.convertToUnits(feeder.getFeedStartLocation().getUnits());
				feedStartX.setText(l.getLengthX().toString());
				feedStartY.setText(l.getLengthY().toString());
				feedStartZ.setText(l.getLengthZ().toString());
			}
		});

		panelVision = new JPanel();
		panelVision.setBorder(new TitledBorder(null, "Vision",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelFields.add(panelVision);
		panelVision.setLayout(new BoxLayout(panelVision, BoxLayout.Y_AXIS));

		panelVisionEnabled = new JPanel();
		FlowLayout fl_panelVisionEnabled = (FlowLayout) panelVisionEnabled
				.getLayout();
		fl_panelVisionEnabled.setAlignment(FlowLayout.LEFT);
		panelVision.add(panelVisionEnabled);

		chckbxVisionEnabled = new JCheckBox("Vision Enabled?");
		panelVisionEnabled.add(chckbxVisionEnabled);

		separator = new JSeparator();
		panelVision.add(separator);

		panelImageTemplate = new JPanel();
		panelVision.add(panelImageTemplate);
		panelImageTemplate.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		lblTemplateImage = new JLabel("Template Image");
		panelImageTemplate.add(lblTemplateImage, "2, 2, center, default");

		labelTemplateImage = new JLabel("");
		labelTemplateImage.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		labelTemplateImage.setMinimumSize(new Dimension(150, 150));
		labelTemplateImage.setMaximumSize(new Dimension(150, 150));
		labelTemplateImage.setHorizontalAlignment(SwingConstants.CENTER);
		labelTemplateImage.setSize(new Dimension(150, 150));
		labelTemplateImage.setPreferredSize(new Dimension(150, 150));
		labelTemplateImage.setIcon(new ImageIcon(
				"/Users/jason/Desktop/snap.png"));
		panelImageTemplate.add(labelTemplateImage, "2, 4, center, default");

		btnNewTemplateImage = new JButton(newTemplateImageAction);
		panelImageTemplate.add(btnNewTemplateImage, "2, 6");
		scrollPane.setBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		JPanel panelActions = new JPanel();
		panelActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(panelActions, BorderLayout.SOUTH);

		btnCancel = new JButton(cancelAction);
		panelActions.add(btnCancel);

		btnSave = new JButton(saveAction);
		panelActions.add(btnSave);

		createBindings();
		loadFromModel();
	}

	private void createBindings() {
		LengthConverter lengthConverter = new LengthConverter();
		DoubleConverter doubleConverter = new DoubleConverter("%2.3f");
		BindingListener listener = new AbstractBindingListener() {
			@Override
			public void synced(Binding binding) {
				saveAction.setEnabled(true);
				cancelAction.setEnabled(true);
			}
		};

		wrappedBindings.add(JBindings.bind(feeder, "feedRate", feedRate,
				"text", doubleConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "actuatorId",
				textFieldActuatorId, "text", listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedStartLocation.lengthX",
				feedStartX, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedStartLocation.lengthY",
				feedStartY, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedStartLocation.lengthZ",
				feedStartZ, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedEndLocation.lengthX",
				feedEndX, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedEndLocation.lengthY",
				feedEndY, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedEndLocation.lengthZ",
				feedEndZ, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "vision.enabled",
				chckbxVisionEnabled, "selected", null, listener));
	}

	private void loadFromModel() {
		for (WrappedBinding wrappedBinding : wrappedBindings) {
			wrappedBinding.reset();
		}
		saveAction.setEnabled(false);
		cancelAction.setEnabled(false);
	}

	private void saveToModel() {
		for (WrappedBinding wrappedBinding : wrappedBindings) {
			wrappedBinding.save();
		}
		saveAction.setEnabled(false);
		cancelAction.setEnabled(false);
	}

	@Override
	public void setWizardContainer(WizardContainer wizardContainer) {
		this.wizardContainer = wizardContainer;
	}

	@Override
	public JPanel getWizardPanel() {
		return this;
	}

	@Override
	public String getWizardName() {
		// TODO Auto-generated method stub
		return null;
	}

	private Action saveAction = new AbstractAction("Apply") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveToModel();
			wizardContainer
					.wizardCompleted(ReferenceTapeFeederConfigurationWizard.this);
		}
	};

	private Action cancelAction = new AbstractAction("Reset") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			loadFromModel();
		}
	};

	private Action newTemplateImageAction = new AbstractAction(
			"New Template Image") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			BufferedImage image = MainFrame.cameraPanel.getSelectedCameraView().getSelectionRectangleImage();
			if (image == null) {
				labelTemplateImage.setIcon(null);
			}
			else {
				labelTemplateImage.setIcon(new ImageIcon(image));
			}
		}
	};
	private JButton btnCancel;
	private JLabel lblActuatorId;
	private JTextField textFieldActuatorId;
	private JPanel panelGeneral;
	private JPanel panelVision;
	private JPanel panelLocations;
	private JCheckBox chckbxVisionEnabled;
	private JPanel panelVisionEnabled;
	private JPanel panelImageTemplate;
	private JLabel lblTemplateImage;
	private JLabel labelTemplateImage;
	private JButton btnNewTemplateImage;
	private JSeparator separator;
}