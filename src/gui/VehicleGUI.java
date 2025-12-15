package gui;

import services.VehicleService;
import data.VehicleDatabaseRepository;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import models.*;

/**
 * A minimal Swing GUI to view and add vehicles
 */
public class VehicleGUI extends JFrame {
    private final VehicleService service;
    private final DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;

    public VehicleGUI() {
        this(new VehicleService(new VehicleDatabaseRepository()));
    }

    public VehicleGUI(VehicleService service) {
        super("Vehicle Manager");
        this.service = service;
        try { this.service.loadFromJson("vehicles.json"); } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Try to use Nimbus for a more modern look if available
        try { UIManager.setLookAndFeel(new NimbusLookAndFeel()); SwingUtilities.updateComponentTreeUI(this); } catch (Exception ignored) {}
        setSize(900, 600);
        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new String[]{"ID","Type", "Brand", "Model", "Year", "Details"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        // header styling
        JTableHeader hdr = table.getTableHeader();
        hdr.setBackground(new Color(230,230,230)); hdr.setFont(hdr.getFont().deriveFont(Font.BOLD, 13f));
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) hdr.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // set custom renderer for alternating rows & details column
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (col == 4 && c instanceof JLabel) { ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER); }
                if (!isSelected) { c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248,248,248)); c.setForeground(Color.DARK_GRAY);} else { c.setBackground(new Color(50,115,210)); c.setForeground(Color.WHITE); }
                if (col == 5) { setFont(getFont().deriveFont(Font.ITALIC)); setForeground(new Color(100,100,100)); }
                return c;
            }
        });
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        // Column sizing
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(240);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        // double-click to edit rows
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { if (e.getClickCount() == 2) { doEditSelected(); } }
        });
        add(scroll, BorderLayout.CENTER);

        // Filters toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JTextField brandFilterField = new JTextField(12);
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"","Car","Bike","Truck","Motorcycle"});
        JButton filterBtn = new JButton("Filter");
        JButton clearFilterBtn = new JButton("Clear");
        // Use JSON-based persistence only
        JButton loadJsonBtn = new JButton("📂 Load JSON");
        JButton saveJsonBtn = new JButton("🧾 Save JSON");
        toolbar.add(new JLabel("Brand:")); toolbar.add(brandFilterField); toolbar.add(new JLabel("Type:")); toolbar.add(typeFilter); toolbar.add(filterBtn); toolbar.add(clearFilterBtn);
        toolbar.addSeparator(); toolbar.add(loadJsonBtn); toolbar.add(saveJsonBtn);
        toolbar.add(Box.createHorizontalStrut(10));
        JButton quickAdd = new JButton("➕ Add"); quickAdd.setToolTipText("Open Add form"); quickAdd.setMnemonic('A');
        JCheckBox showIds = new JCheckBox("Show IDs"); showIds.setToolTipText("Toggle ID column visibility");
        showIds.addActionListener(ev -> {
            javax.swing.table.TableColumn col = table.getColumnModel().getColumn(0);
            if (showIds.isSelected()) {
                col.setMinWidth(120);
                col.setMaxWidth(300);
                col.setPreferredWidth(120);
            } else {
                col.setMinWidth(0);
                col.setMaxWidth(0);
                col.setPreferredWidth(0);
            }
            // force layout refresh
            table.revalidate();
            table.repaint();
        });
        toolbar.add(quickAdd);
        toolbar.add(showIds);
        add(toolbar, BorderLayout.NORTH);
        // highlight quickAdd mnemonic for accessibility
        quickAdd.setFocusPainted(true);
        quickAdd.addActionListener(e -> doAddDialog());

        JPanel form = new JPanel(new BorderLayout());
        JPanel fieldPanel = new JPanel(new GridLayout(0, 2, 6, 6));
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Car","Bike","Truck","Motorcycle"});
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2023, 1900, 2100, 1));
        fieldPanel.add(new JLabel("Type:")); fieldPanel.add(typeCombo);
        fieldPanel.add(new JLabel("Brand:")); fieldPanel.add(brandField);
        fieldPanel.add(new JLabel("Model:")); fieldPanel.add(modelField);
        fieldPanel.add(new JLabel("Year:")); fieldPanel.add(yearSpinner);

        // Add type-specific panels (CardLayout)
        JPanel typeSpecificAdd = new JPanel(new CardLayout());
        JPanel carPanelAdd = new JPanel(new GridLayout(0,2));
        JSpinner doorsAddSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 99, 1));
        JTextField fuelAddField = new JTextField("Petrol");
        carPanelAdd.add(new JLabel("Doors:")); carPanelAdd.add(doorsAddSpinner); carPanelAdd.add(new JLabel("Fuel:")); carPanelAdd.add(fuelAddField);
        JPanel bikePanelAdd = new JPanel(new GridLayout(0,2));
        JCheckBox sideAddCheck = new JCheckBox(); JTextField bikeCatAdd = new JTextField("Cruiser");
        bikePanelAdd.add(new JLabel("Sidecar:")); bikePanelAdd.add(sideAddCheck); bikePanelAdd.add(new JLabel("Category:")); bikePanelAdd.add(bikeCatAdd);
        JPanel truckPanelAdd = new JPanel(new GridLayout(0,2));
        JSpinner payloadAddSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1)); JCheckBox trailerAddCheck = new JCheckBox();
        truckPanelAdd.add(new JLabel("Payload:")); truckPanelAdd.add(payloadAddSpinner); truckPanelAdd.add(new JLabel("Trailer:")); truckPanelAdd.add(trailerAddCheck);
        JPanel motorPanelAdd = new JPanel(new GridLayout(0,2));
        JSpinner ccAddSpinner = new JSpinner(new SpinnerNumberModel(500, 50, 5000, 50)); JTextField motorCatAdd = new JTextField("Sports");
        motorPanelAdd.add(new JLabel("Engine CC:")); motorPanelAdd.add(ccAddSpinner); motorPanelAdd.add(new JLabel("Category:")); motorPanelAdd.add(motorCatAdd);
        typeSpecificAdd.add(carPanelAdd, "Car"); typeSpecificAdd.add(bikePanelAdd, "Bike"); typeSpecificAdd.add(truckPanelAdd, "Truck"); typeSpecificAdd.add(motorPanelAdd, "Motorcycle");
        // Add to main form
        form.add(fieldPanel, BorderLayout.NORTH);
        form.add(typeSpecificAdd, BorderLayout.CENTER);

        JButton addBtn = new JButton("➕ Add"); addBtn.setToolTipText("Add vehicle using the bottom form");
        JButton deleteBtn = new JButton("🗑️ Delete"); deleteBtn.setToolTipText("Delete selected vehicle"); deleteBtn.setMnemonic('D');
        JButton editBtn = new JButton("✏️ Edit"); editBtn.setToolTipText("Edit selected vehicle"); editBtn.setMnemonic('E');
        statusLabel = new JLabel("Ready");
        // Default to Car panel
        ((CardLayout)typeSpecificAdd.getLayout()).show(typeSpecificAdd, "Car");
        quickAdd.addActionListener(e -> { typeCombo.requestFocusInWindow(); });
        typeCombo.addActionListener(ae -> { ((CardLayout)typeSpecificAdd.getLayout()).show(typeSpecificAdd, (String)typeCombo.getSelectedItem()); });

        addBtn.addActionListener(e -> {
            String type = ((String)typeCombo.getSelectedItem()).toLowerCase();
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            int year = (Integer) yearSpinner.getValue();
            Vehicle v = null;
            try {
                switch (type) {
                    case "car":
                        // extra: doors,fuel
                        int doors = (Integer) doorsAddSpinner.getValue();
                        String fuel = fuelAddField.getText().trim();
                        v = new Car(brand, model, year, doors, fuel);
                        break;
                    case "bike":
                        boolean sidecar = sideAddCheck.isSelected();
                        String btype = bikeCatAdd.getText().trim();
                        v = new Bike(brand, model, year, sidecar, btype);
                        break;
                    case "truck":
                        double payload = ((Integer) payloadAddSpinner.getValue()).doubleValue();
                        boolean trailer = trailerAddCheck.isSelected();
                        v = new Truck(brand, model, year, payload, trailer);
                        break;
                    case "motorcycle":
                        int cc = (Integer) ccAddSpinner.getValue();
                        String mcat = motorCatAdd.getText().trim();
                        v = new Motorcycle(brand, model, year, cc, mcat);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Unknown type");
                }
                // simple validation
                if (brand.isEmpty() || model.isEmpty()) { JOptionPane.showMessageDialog(this, "Brand and Model are required"); return; }
                if (v != null) {
                    try { service.addVehicle(v); service.saveToJson("vehicles.json"); refreshTable(); statusLabel.setText("Added vehicle: " + brand + " " + model); highlightStatus(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error adding vehicle: " + ex.getMessage()); }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding vehicle: " + ex.getMessage());
            }
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(addBtn); controls.add(editBtn); controls.add(deleteBtn);
        controls.add(Box.createHorizontalStrut(20)); controls.add(statusLabel);

        // Add hover highlight to main action buttons
        makeHoverStyledButton(addBtn);
        makeHoverStyledButton(editBtn);
        makeHoverStyledButton(deleteBtn);
        makeHoverStyledButton(filterBtn);
        makeHoverStyledButton(clearFilterBtn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(form, BorderLayout.CENTER);
        bottom.add(controls, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        // Filter action
        filterBtn.addActionListener(e -> {
            filterTable(brandFilterField.getText().trim(), (String) typeFilter.getSelectedItem());
        });
        // Live filter as you type (debounced)
        final Timer debounce = new Timer(350, ev -> filterTable(brandFilterField.getText().trim(), (String) typeFilter.getSelectedItem()));
        debounce.setRepeats(false);
        brandFilterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { debounce.restart(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { debounce.restart(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { debounce.restart(); }
        });
        typeFilter.addActionListener(e -> filterTable(brandFilterField.getText().trim(), (String) typeFilter.getSelectedItem()));
        clearFilterBtn.addActionListener(e -> { brandFilterField.setText(""); typeFilter.setSelectedIndex(0); refreshTable(); });

        // Save and Load actions
            saveJsonBtn.addActionListener(e -> {
                try {
                    service.saveToJson("vehicles.json");
                    statusLabel.setText("Saved JSON");
                    highlightStatus();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving JSON: " + ex.getMessage());
                }
            });
            saveJsonBtn.setMnemonic('S');
            loadJsonBtn.addActionListener(e -> {
                try {
                    service.loadFromJson("vehicles.json");
                    refreshTable();
                    statusLabel.setText("Loaded JSON");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error loading JSON: " + ex.getMessage());
                }
            });
            loadJsonBtn.setMnemonic('L');
        loadJsonBtn.addActionListener(e -> { try { service.loadFromJson("vehicles.json"); refreshTable(); statusLabel.setText("Loaded JSON"); } catch (Exception ignored) {} }); loadJsonBtn.setMnemonic('O');

        // Delete action
        deleteBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
                if (sel >= 0) {
                int modelRow = table.convertRowIndexToModel(sel);
                String id = (String) tableModel.getValueAt(modelRow, 0);
                int r = JOptionPane.showConfirmDialog(this, "Delete vehicle id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                        try { if (service.removeVehicleById(id)) { service.saveToJson("vehicles.json"); refreshTable(); statusLabel.setText("Deleted vehicle"); highlightStatus(); } } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error deleting vehicle: " + ex.getMessage()); }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No row selected");
            }
        });

        // Edit action: show a dialog to enter new values using a modal dialog, and preserve id
        editBtn.addActionListener(e -> doEditSelected());
    }

    private void makeHoverStyledButton(JButton btn) {
        Color orig = btn.getBackground();
        btn.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(235, 245, 255)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(orig); }
        });
    }

    // Highlight the status label temporarily with a green background
    private void highlightStatus() {
        Color originalBg = statusLabel.getBackground();
        boolean wasOpaque = statusLabel.isOpaque();
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(204, 255, 204));
        Timer t = new Timer(700, ev -> {
            statusLabel.setBackground(originalBg);
            statusLabel.setOpaque(wasOpaque);
            ((Timer)ev.getSource()).stop();
        });
        t.setRepeats(false);
        t.start();
    }

    private void doEditSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "No row selected"); return; }
        int modelRow = table.convertRowIndexToModel(sel);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        Vehicle v;
        try { v = service.getVehicleById(id); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error retrieving vehicle: " + ex.getMessage()); return; }
        if (v == null) { JOptionPane.showMessageDialog(this, "Selected vehicle not found"); return; }
        JComboBox<String> typeEdit = new JComboBox<>(new String[]{"Car","Bike","Truck","Motorcycle"});
        typeEdit.setSelectedItem(v.getClass().getSimpleName());
        JTextField brandEdit = new JTextField(v.getBrand());
        JTextField modelEdit = new JTextField(v.getModel());
        JSpinner yearEdit = new JSpinner(new SpinnerNumberModel(v.getYear(), 1900, 2100, 1));
        JPanel cardPanel = new JPanel(new CardLayout());
        JPanel carPanel = new JPanel(new GridLayout(0, 2));
        JSpinner doorsEdit = new JSpinner(new SpinnerNumberModel(4, 1, 999, 1));
        JTextField fuelEdit = new JTextField();
        carPanel.add(new JLabel("Doors:")); carPanel.add(doorsEdit);
        carPanel.add(new JLabel("Fuel:")); carPanel.add(fuelEdit);
        JPanel bikePanel = new JPanel(new GridLayout(0, 2));
        JCheckBox sideEdit = new JCheckBox();
        JTextField bikeCat = new JTextField(); bikePanel.add(new JLabel("Sidecar:")); bikePanel.add(sideEdit); bikePanel.add(new JLabel("Category:")); bikePanel.add(bikeCat);
        JPanel truckPanel = new JPanel(new GridLayout(0, 2));
        JSpinner payloadEdit = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1));
        JCheckBox trailerEdit = new JCheckBox(); truckPanel.add(new JLabel("Payload:")); truckPanel.add(payloadEdit); truckPanel.add(new JLabel("Trailer:")); truckPanel.add(trailerEdit);
        JPanel motorPanel = new JPanel(new GridLayout(0, 2));
        JSpinner ccEdit = new JSpinner(new SpinnerNumberModel(500, 50, 5000, 50));
        JTextField motorCat = new JTextField(); motorPanel.add(new JLabel("CC:")); motorPanel.add(ccEdit); motorPanel.add(new JLabel("Category:")); motorPanel.add(motorCat);
        cardPanel.add(carPanel, "Car"); cardPanel.add(bikePanel, "Bike"); cardPanel.add(truckPanel, "Truck"); cardPanel.add(motorPanel, "Motorcycle");
        typeEdit.addActionListener(ae -> { ((CardLayout) cardPanel.getLayout()).show(cardPanel, (String)typeEdit.getSelectedItem()); });
        if (v instanceof Car) { Car c = (Car)v; doorsEdit.setValue(c.getNumDoors()); fuelEdit.setText(c.getFuelType()); ((CardLayout) cardPanel.getLayout()).show(cardPanel, "Car"); }
        else if (v instanceof Bike) { Bike b = (Bike)v; sideEdit.setSelected(b.hasSidecar()); bikeCat.setText(b.getType()); ((CardLayout) cardPanel.getLayout()).show(cardPanel, "Bike"); }
        else if (v instanceof Truck) { Truck t = (Truck)v; payloadEdit.setValue((int) t.getPayloadCapacityKg()); trailerEdit.setSelected(t.hasTrailer()); ((CardLayout) cardPanel.getLayout()).show(cardPanel, "Truck"); }
        else if (v instanceof Motorcycle) { Motorcycle m = (Motorcycle)v; ccEdit.setValue(m.getEngineCc()); motorCat.setText(m.getCategory()); ((CardLayout) cardPanel.getLayout()).show(cardPanel, "Motorcycle"); }
        JPanel panel = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new GridLayout(0,2)); top.add(new JLabel("Type:")); top.add(typeEdit); top.add(new JLabel("Brand:")); top.add(brandEdit); top.add(new JLabel("Model:")); top.add(modelEdit); top.add(new JLabel("Year:")); top.add(yearEdit);
        panel.add(top, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit vehicle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String bbrand = brandEdit.getText().trim();
                String bmodel = modelEdit.getText().trim();
                int byear = (Integer) ((JSpinner) yearEdit).getValue();
                String tname = ((String) typeEdit.getSelectedItem()).toLowerCase();
                if (tname.equals("car")) {
                    int doors = (Integer) doorsEdit.getValue();
                    String fuel = fuelEdit.getText().trim();
                    try { service.updateVehicle(new Car(id, bbrand, bmodel, byear, doors, fuel)); } catch (Exception ex) { throw ex; }
                } else if (tname.equals("bike")) {
                    boolean side = sideEdit.isSelected();
                    String cat = bikeCat.getText().trim();
                    try { service.updateVehicle(new Bike(id, bbrand, bmodel, byear, side, cat)); } catch (Exception ex) { throw ex; }
                } else if (tname.equals("truck")) {
                    double payload = ((Integer) payloadEdit.getValue()).doubleValue();
                    boolean trailer = trailerEdit.isSelected();
                    try { service.updateVehicle(new Truck(id, bbrand, bmodel, byear, payload, trailer)); } catch (Exception ex) { throw ex; }
                } else if (tname.equals("motorcycle")) {
                    int cc = (Integer) ccEdit.getValue();
                    String cat = motorCat.getText().trim();
                    try { service.updateVehicle(new Motorcycle(id, bbrand, bmodel, byear, cc, cat)); } catch (Exception ex) { throw ex; }
                }
                try { service.saveToJson("vehicles.json"); } catch (Exception ignored) {}
                refreshTable();
                statusLabel.setText("Updated vehicle: " + bbrand + " " + bmodel); highlightStatus();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error editing vehicle: " + ex.getMessage());
            }
        }
    }

    private void doAddDialog() {
        JComboBox<String> typeEdit = new JComboBox<>(new String[]{"Car","Bike","Truck","Motorcycle"});
        typeEdit.setSelectedIndex(0);
        JTextField brandAdd = new JTextField();
        JTextField modelAdd = new JTextField();
        JSpinner yearAdd = new JSpinner(new SpinnerNumberModel(2023, 1900, 2100, 1));
        JPanel cardPanel = new JPanel(new CardLayout());
        JPanel carPanel = new JPanel(new GridLayout(0,2));
        JSpinner doorsAdd = new JSpinner(new SpinnerNumberModel(4,1,999,1)); JTextField fuelAdd = new JTextField("Petrol");
        carPanel.add(new JLabel("Doors:")); carPanel.add(doorsAdd); carPanel.add(new JLabel("Fuel:")); carPanel.add(fuelAdd);
        JPanel bikePanel = new JPanel(new GridLayout(0,2)); JCheckBox sideAdd = new JCheckBox(); JTextField bikeCatAdd = new JTextField("Cruiser"); bikePanel.add(new JLabel("Sidecar:")); bikePanel.add(sideAdd); bikePanel.add(new JLabel("Category:")); bikePanel.add(bikeCatAdd);
        JPanel truckPanel = new JPanel(new GridLayout(0,2)); JSpinner payloadAdd = new JSpinner(new SpinnerNumberModel(0,0,9999999,1)); JCheckBox trailerAdd = new JCheckBox(); truckPanel.add(new JLabel("Payload:")); truckPanel.add(payloadAdd); truckPanel.add(new JLabel("Trailer:")); truckPanel.add(trailerAdd);
        JPanel motorPanel = new JPanel(new GridLayout(0,2)); JSpinner ccAdd = new JSpinner(new SpinnerNumberModel(500,50,5000,50)); JTextField motorCatAdd = new JTextField("Sports"); motorPanel.add(new JLabel("Engine CC:")); motorPanel.add(ccAdd); motorPanel.add(new JLabel("Category:")); motorPanel.add(motorCatAdd);
        cardPanel.add(carPanel, "Car"); cardPanel.add(bikePanel, "Bike"); cardPanel.add(truckPanel, "Truck"); cardPanel.add(motorPanel, "Motorcycle");
        typeEdit.addActionListener(ae -> { ((CardLayout) cardPanel.getLayout()).show(cardPanel, (String)typeEdit.getSelectedItem()); });
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "Car");
        JPanel panel = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new GridLayout(0,2)); top.add(new JLabel("Type:")); top.add(typeEdit); top.add(new JLabel("Brand:")); top.add(brandAdd); top.add(new JLabel("Model:")); top.add(modelAdd); top.add(new JLabel("Year:")); top.add(yearAdd);
        panel.add(top, BorderLayout.NORTH); panel.add(cardPanel, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add vehicle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String type = ((String) typeEdit.getSelectedItem()).toLowerCase();
                String brand = brandAdd.getText().trim();
                String model = modelAdd.getText().trim();
                int year = (Integer) yearAdd.getValue();
                // validation
                if (brand.isEmpty() || model.isEmpty()) { JOptionPane.showMessageDialog(this, "Brand and Model are required"); return; }
                if (year < 1900 || year > 2100) { JOptionPane.showMessageDialog(this, "Year must be between 1900 and 2100"); return; }
                Vehicle v = null;
                switch (type) {
                    case "car": int doors = (Integer) doorsAdd.getValue(); String fuel = fuelAdd.getText().trim(); v = new Car(brand, model, year, doors, fuel); break;
                    case "bike": boolean side = sideAdd.isSelected(); String cat = bikeCatAdd.getText().trim(); v = new Bike(brand, model, year, side, cat); break;
                    case "truck": double payload = ((Integer) payloadAdd.getValue()).doubleValue(); boolean tr = trailerAdd.isSelected(); v = new Truck(brand, model, year, payload, tr); break;
                    case "motorcycle": int cc = (Integer) ccAdd.getValue(); String mcat = motorCatAdd.getText().trim(); v = new Motorcycle(brand, model, year, cc, mcat); break;
                }
                if (v != null) { try { service.addVehicle(v); service.saveToJson("vehicles.json"); refreshTable(); statusLabel.setText("Added vehicle: " + brand + " " + model); highlightStatus(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error adding vehicle: " + ex.getMessage()); } }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error adding vehicle: " + ex.getMessage()); }
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Vehicle> list = new ArrayList<>();
        try { list = service.getAllVehicles(); } catch (Exception ignored) {}
        for (Vehicle v : list) {
            String type = v.getClass().getSimpleName();
            String details = "";
            if (v instanceof Car) {
                Car c = (Car) v;
                details = "Doors:" + c.getNumDoors() + ", Fuel:" + c.getFuelType();
            } else if (v instanceof Bike) {
                Bike b = (Bike) v;
                details = "Sidecar:" + b.hasSidecar() + ", Category:" + b.getType();
            } else if (v instanceof Truck) {
                Truck t = (Truck) v;
                details = "Payload:" + t.getPayloadCapacityKg() + ", Trailer:" + t.hasTrailer();
            } else if (v instanceof Motorcycle) {
                Motorcycle m = (Motorcycle) v;
                details = "CC:" + m.getEngineCc() + ", Cat:" + m.getCategory();
            }
            tableModel.addRow(new Object[]{v.getId(), type, v.getBrand(), v.getModel(), v.getYear(), details});
        }
        // note: mouse listener for double-click is added once in the constructor
    }

    private void addVehicleToTable(Vehicle v) {
        String type = v.getClass().getSimpleName();
        String details = "";
        if (v instanceof Car) { Car c = (Car) v; details = "Doors:" + c.getNumDoors() + ", Fuel:" + c.getFuelType(); }
        else if (v instanceof Bike) { Bike b = (Bike) v; details = "Sidecar:" + b.hasSidecar() + ", Category:" + b.getType(); }
        else if (v instanceof Truck) { Truck t = (Truck) v; details = "Payload:" + t.getPayloadCapacityKg() + ", Trailer:" + t.hasTrailer(); }
        else if (v instanceof Motorcycle) { Motorcycle m = (Motorcycle) v; details = "CC:" + m.getEngineCc() + ", Cat:" + m.getCategory(); }
        tableModel.addRow(new Object[]{v.getId(), type, v.getBrand(), v.getModel(), v.getYear(), details});
    }

    private void filterTable(String brandFilter, String typeFilter) {
        brandFilter = brandFilter == null ? "" : brandFilter.trim();
        typeFilter = typeFilter == null ? "" : typeFilter.trim();
        if (brandFilter.isEmpty() && typeFilter.isEmpty()) { refreshTable(); return; }
        tableModel.setRowCount(0);
        List<Vehicle> list = new ArrayList<>();
        try { list = service.getAllVehicles(); } catch (Exception ignored) {}
        for (Vehicle v : list) {
                boolean okBrand = brandFilter.isEmpty() || (v.getBrand() != null && v.getBrand().toLowerCase().contains(brandFilter.toLowerCase()));
            boolean okType = (typeFilter == null || typeFilter.isEmpty()) || v.getClass().getSimpleName().equalsIgnoreCase(typeFilter);
            if (okBrand && okType) { addVehicleToTable(v); }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VehicleGUI gui = new VehicleGUI();
            gui.setVisible(true);
        });
    }
}
