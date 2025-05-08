import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;

// Abstract class for SmartDevice implementing Serializable interface
abstract class SmartDevice implements Serializable {
    private static final long serialVersionUID = 1L;//yeh basically aik unique id hai jo humne manually set kiya hai
    private String name;
    private boolean isOn;

    public SmartDevice(String name) {
        this.name = name;
        this.isOn = false;
    }

    public String getName() {
        return name;
    }

    public boolean isOn() {
        return isOn;
    }

    public void turnOn() {
        isOn = true;
    }

    public void turnOff() {
        isOn = false;
    }

    protected JPanel createPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true), getName()));
        panel.setBackground(new Color(230, 230, 250));
        panel.setPreferredSize(new Dimension(300, 200));
        return panel;
    }

    public abstract JPanel getControlPanel(Room room, JPanel devicePanel);
}

class SmartLight extends SmartDevice {
    private static final long serialVersionUID = 1L;
    private int brightness;
    private boolean hasIntensityControl;

    public SmartLight(String name, boolean hasIntensityControl) {
        super(name);
        this.brightness = 50;
        this.hasIntensityControl = hasIntensityControl;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    @Override
    //Smart light ka panel hai jab hum add a smart light krte tou basically yeh panel create hota
    public JPanel getControlPanel(Room room, JPanel devicePanel) {
        JPanel panel = createPanel();

        JLabel stateLabel = new JLabel("State: " + (isOn() ? "On" : "Off"));// state show krta hai
        JButton toggleButton = new JButton(isOn() ? "Turn Off" : "Turn On");//yeh on off wala button hai aur conditional operator basically text change krta depending upon the condition

        toggleButton.addActionListener(e -> {
            try {
                if (isOn()) {
                    turnOff();
                    stateLabel.setText("State: Off");
                    toggleButton.setText("Turn On");
                } else {
                    turnOn();
                    stateLabel.setText("State: On");
                    toggleButton.setText("Turn Off");
                }
                ((SmartHomeController) SwingUtilities.getWindowAncestor(panel)).setStateSaved(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error toggling light state: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(stateLabel);
        panel.add(Box.createHorizontalStrut(20));//yeh basically space create krta hai to visually the panel to look better
        panel.add(toggleButton);
        if (hasIntensityControl) {
            JLabel brightnessLabel = new JLabel("Brightness: 50%");
            JSlider brightnessSlider = new JSlider(30, 100, brightness);// yeh basically aik slider hai jis se brightness adjust hoti hai

            brightnessSlider.setPaintTicks(true);//tick marks enable krta hai
            brightnessSlider.setPaintLabels(true);// yeh slider pe value show krta hai
            brightnessSlider.setMajorTickSpacing(20);//units define kr rhe idhr in the slider
            brightnessSlider.setMinorTickSpacing(5);//same goes for this
            brightnessSlider.addChangeListener(e -> {
                try {
                    setBrightness(brightnessSlider.getValue());//jab hum brightness update kren tou updated brightness show ho isliye humne yeh add kiya
                    brightnessLabel.setText("Brightness: " + brightnessSlider.getValue() + "%");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error adjusting brightness: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(new JLabel("Adjust Brightness:"));
            panel.add(brightnessSlider);
            panel.add(brightnessLabel);
        }
        return panel;
    }
}

class SmartThermostat extends SmartDevice {
    private static final long serialVersionUID = 1L;
    private int temperature;
    private boolean hasIntensityControl;

    public SmartThermostat(String name, boolean hasIntensityControl) {
        super(name);
        this.temperature = 22;
        this.hasIntensityControl = hasIntensityControl;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public JPanel getControlPanel(Room room, JPanel devicePanel) {
        JPanel panel = createPanel();

        JLabel stateLabel = new JLabel("State: " + (isOn() ? "On" : "Off"));
        JButton toggleButton = new JButton(isOn() ? "Turn Off" : "Turn On");


        toggleButton.addActionListener(e -> {
            try {
                if (isOn()) {
                    turnOff();
                    stateLabel.setText("State: Off");
                    toggleButton.setText("Turn On");
                } else {
                    turnOn();
                    stateLabel.setText("State: On");
                    toggleButton.setText("Turn Off");
                }
                ((SmartHomeController) SwingUtilities.getWindowAncestor(panel)).setStateSaved(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error toggling thermostat state: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(stateLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(toggleButton);
        if (hasIntensityControl) {
            JLabel tempLabel = new JLabel("Temperature: " + temperature + "°C");
            JSlider tempSlider = new JSlider(0, 30 , temperature);

            tempSlider.setPaintTicks(true);
            tempSlider.setPaintLabels(true);
            tempSlider.setMajorTickSpacing(5);
            tempSlider.setMinorTickSpacing(1);
            tempSlider.addChangeListener(e -> {
                try {
                    setTemperature(tempSlider.getValue());
                    stateLabel.setText("State: " + (isOn() ? "On" : "Off"));
                    tempLabel.setText("Temperature: " + temperature + "°C");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error adjusting temperature: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(Box.createHorizontalStrut(170));
            panel.add(new JLabel("Adjust Temperature:"));
            panel.add(tempSlider);
            panel.add(tempLabel);
        }
        return panel;
    }
}

class SmartCamera extends SmartDevice {
    private static final long serialVersionUID = 1L;
    private boolean recording;

    public SmartCamera(String name) {
        super(name);
        this.recording = false;
    }

    public boolean isRecording() {
        return recording;
    }

    public void startRecording() {
        recording = true;
    }

    public void stopRecording() {
        recording = false;
    }

    @Override
    public JPanel getControlPanel(Room room, JPanel devicePanel) {
        JPanel panel = createPanel();
        JLabel stateLabel = new JLabel("State: " + (isOn() ? "On" : "Off"));
        JLabel RecordingLabel = new JLabel("Recording: " + (recording ? "Yes" : "No"));
        JButton toggleButton = new JButton(isOn() ? "Turn Off" : "Turn On");
        JButton recordButton = new JButton(recording ? "Stop Recording" : "Start Recording");

        toggleButton.addActionListener(e -> {
            try {
                if (isOn()) {
                    turnOff();
                    if (recording) {
                        JOptionPane.showMessageDialog(panel, "The camera will stop recording.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        stopRecording();
                    }
                    recordButton.setText(recording ? "Stop Recording" : "Start Recording");
                    stateLabel.setText("State: Off");
                    toggleButton.setText("Turn On");
                } else {
                    turnOn();
                    stateLabel.setText("State: On");
                    toggleButton.setText("Turn Off");
                }
                ((SmartHomeController) SwingUtilities.getWindowAncestor(panel)).setStateSaved(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error toggling camera state: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        recordButton.addActionListener(e -> {
            try {
                if (isOn()) {
                    if (recording) {
                        stopRecording();
                        RecordingLabel.setText("Recording: " + (recording ? "Yes" : "No"));
                        recordButton.setText("Start Recording");
                    } else {
                        startRecording();
                        RecordingLabel.setText("Recording: " + (recording ? "Yes" : "No"));
                        recordButton.setText("Stop Recording");
                    }
                    stateLabel.setText("State: " + (isOn() ? "On" : "Off"));

                } else {
                    JOptionPane.showMessageDialog(panel, "Turn on the camera first.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                ((SmartHomeController) SwingUtilities.getWindowAncestor(panel)).setStateSaved(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error toggling recording state: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(stateLabel);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(toggleButton);
        panel.add(Box.createHorizontalStrut(100));
        panel.add(RecordingLabel);
        panel.add(recordButton);

        return panel;
    }
}

class Room implements Serializable {
    private String name;
    private ArrayList<SmartDevice> devices;//yeh arraylist hai jis me hum devices store krte hain

    public Room(String name) {
        this.name = name;
        this.devices = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<SmartDevice> getDevices() {
        return devices;
    }

    public void addDevice(SmartDevice device) {
        devices.add(device);
    }

    public void removeDevice(SmartDevice device) {
        devices.remove(device);
    }
}

public class SmartHomeController extends JFrame {

    private JTabbedPane roomTabbedPane;//yeh tabbed pane hai jis me hum rooms add krte hain
    private ArrayList<Room> rooms;//yeh arraylist hai jis me hum rooms store krte hain
    private JLabel statusBar;//yeh status bar hai jis me hum status show krte hain
    private boolean isStateSaved = false;//yeh check krta hai k state save hui hai ya nahi

    public SmartHomeController() {
        rooms = new ArrayList<>();
        setTitle("Smart Home Controller");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);//Main window ka size
        initializeUI();
        setVisible(true);

        addWindowListener(new WindowAdapter() {

            @Override// yeh basically window close krne pr confirm krta hai k state save krna hai ya nahi
            public void windowClosing(WindowEvent e) {
                if (isStateSaved) {
                    System.exit(0);
                } else {
                    int option = JOptionPane.showConfirmDialog(SmartHomeController.this, "Do you want to save state?", "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (option == JOptionPane.YES_OPTION) {
                        saveState();
                        System.exit(0);
                    } else if (option == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    }

                }
            }
        });
    }

    public static void updateDevicePanel(JPanel devicePanel, Room room, Class<? extends SmartDevice> deviceClass) {//Class<? extends SmartDevice> basically aik generic class hai jis me hum SmartDevice class ya uski subclasses pass kr skte hain
        devicePanel.removeAll();
        for (SmartDevice device : room.getDevices()) {//room.getDevices() basically aik arraylist return krta hai jis me hum devices store krte hain
            if (deviceClass.isInstance(device)) {//yeh check krta hai k deviceClass jo pass hua hai woh device ka instance hai ya nahi
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                panel.add(device.getControlPanel(room, devicePanel));//yeh device ka control panel add krta hai
                devicePanel.add(panel);
            }
        }
        devicePanel.revalidate();//yeh basically panel ko refresh krta hai
        devicePanel.repaint();//yeh panel ko repaint krta hai
    }

    public void setStateSaved(boolean stateSaved) {
        isStateSaved = stateSaved;
    }

    private void initializeUI() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save State");
        JMenuItem loadMenuItem = new JMenuItem("Load State");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        saveMenuItem.addActionListener(e -> saveState());
        loadMenuItem.addActionListener(e -> loadState());
        exitMenuItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Smart Home Controller v1.0", "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addRoomButton = new JButton("Add Room");
        JButton removeRoomButton = new JButton("Remove Room");
        JButton saveStateButton = new JButton("Save State");
        JButton loadStateButton = new JButton("Load State");

        addRoomButton.setToolTipText("Add a new room");
        removeRoomButton.setToolTipText("Remove an existing room");
        saveStateButton.setToolTipText("Save the current state of rooms and devices");
        loadStateButton.setToolTipText("Load a previously saved state of rooms and devices");

        navPanel.add(addRoomButton);
        navPanel.add(removeRoomButton);
        navPanel.add(saveStateButton);
        navPanel.add(loadStateButton);

        addRoomButton.addActionListener(e -> {
            addRoom();
            setStateSaved(false);
        });
        removeRoomButton.addActionListener(e -> {
            removeRoom();
            setStateSaved(false);
        });
        saveStateButton.addActionListener(e -> saveState());
        loadStateButton.addActionListener(e -> loadState());

        roomTabbedPane = new JTabbedPane();
        statusBar = new JLabel("Ready");
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        add(navPanel, BorderLayout.NORTH);
        add(roomTabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void addRoom() {
        String roomName = JOptionPane.showInputDialog(this, "Enter room name:");
        if (roomName != null && !roomName.trim().isEmpty()) {//agar room name null nahi hai aur empty nahi hai tou yeh loop chalega
            for (Room room : rooms) {
                if (room.getName().equals(roomName)) {//agar room name already exist krta hai tou yeh error show krega
                    JOptionPane.showMessageDialog(this, "Room with this name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Room room = new Room(roomName);
            rooms.add(room);
            updateRoomTabs();
            statusBar.setText("Room added: " + roomName);
        }
    }

    private void removeRoom() {
        String[] roomNames = rooms.stream().map(Room::getName).toArray(String[]::new);/* yeh hum basically aik list bna rhe to display the names of the rooms to the user
        rooms: This is an ArrayList of Room objects.
        stream(): Array list ko stream me convert krta hai
        map(Room::getName): yeh sary rooms ka name le kr return krta
        toArray(String[]::new): yeh nayi array me room names store kr rha.*/
        String selectedRoom = (String) JOptionPane.showInputDialog(this, "Select a room:", "Remove Room", JOptionPane.QUESTION_MESSAGE, null, roomNames, roomNames[0]);

        if (selectedRoom != null) {
            rooms.removeIf(room -> room.getName().equals(selectedRoom));//agar room name select hua hai tou usko remove krdega
            updateRoomTabs();
            statusBar.setText("Room removed: " + selectedRoom);
        }
    }

    private void updateRoomTabs() {
        roomTabbedPane.removeAll();//yeh tabbed pane ko clear krta hai
        for (Room room : rooms) {
            JTabbedPane deviceTabbedPane = new JTabbedPane();//
            deviceTabbedPane.addTab("Smart Light", createDevicePanel(room, SmartLight.class));//smartlight.class basically aik reflection hai of the smartlight class
            deviceTabbedPane.addTab("Smart Thermostat", createDevicePanel(room, SmartThermostat.class));
            deviceTabbedPane.addTab("Smart Camera", createDevicePanel(room, SmartCamera.class));
            roomTabbedPane.addTab(room.getName(), deviceTabbedPane);
        }
    }

    private JPanel createDevicePanel(Room room, Class<? extends SmartDevice> deviceClass) {//Class<? extends SmartDevice> basically aik generic class hai jis me hum SmartDevice class ya uski subclasses pass kr skte hain
        JPanel panel = new JPanel(new BorderLayout());
        JPanel devicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JScrollPane scrollPane = new JScrollPane(devicePanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addDeviceButton = new JButton("Add Device");
        JButton removeDeviceButton = new JButton("Remove Device");

        addDeviceButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(3, 2));
            JLabel nameLabel = new JLabel("Device Name:");
            JTextField nameField = new JTextField();
            JLabel optionLabel = new JLabel("Intensity Control:");
            JRadioButton yesButton = new JRadioButton("Yes");
            JRadioButton noButton = new JRadioButton("No");
            ButtonGroup group = new ButtonGroup();
            group.add(yesButton);
            group.add(noButton);
            yesButton.setSelected(true);

            inputPanel.add(nameLabel);
            inputPanel.add(nameField);
            if (!deviceClass.equals(SmartCamera.class)) {
                inputPanel.add(optionLabel);
                inputPanel.add(yesButton);
                inputPanel.add(new JLabel()); // Empty cell
                inputPanel.add(noButton);
            }
            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Enter Device Details", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String deviceName = nameField.getText();
                boolean hasIntensityControl = yesButton.isSelected();

                if (deviceName != null && !deviceName.trim().isEmpty()) {
                    for (SmartDevice device : room.getDevices()) {
                        if (device.getName().equals(deviceName)) {
                            JOptionPane.showMessageDialog(this, "Device with this name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    try {
                        SmartDevice device;
                        if (deviceClass.equals(SmartLight.class)) {
                            device = new SmartLight(deviceName, hasIntensityControl);
                        } else if (deviceClass.equals(SmartThermostat.class)) {
                            device = new SmartThermostat(deviceName, hasIntensityControl);
                        } else {
                            device = deviceClass.getConstructor(String.class).newInstance(deviceName);
                        }
                        room.addDevice(device);
                        updateDevicePanel(devicePanel, room, deviceClass);
                        statusBar.setText("Device added: " + deviceName);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error adding device: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        removeDeviceButton.addActionListener(e -> {
            String[] deviceNames = room.getDevices().stream().map(SmartDevice::getName).toArray(String[]::new);
            String selectedDevice = (String) JOptionPane.showInputDialog(this, "Select a device:", "Remove Device", JOptionPane.QUESTION_MESSAGE, null, deviceNames, deviceNames[0]);

            if (selectedDevice != null) {
                room.removeDevice(room.getDevices().stream().filter(device -> device.getName().equals(selectedDevice)).findFirst().orElse(null));
                updateDevicePanel(devicePanel, room, deviceClass);
                updateRoomTabs();
                statusBar.setText("Device removed: " + selectedDevice);
            }
        });
        buttonPanel.add(addDeviceButton);
        buttonPanel.add(removeDeviceButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        updateDevicePanel(devicePanel, room, deviceClass);

        return panel;
    }

    private void saveState() {
        String stateName = JOptionPane.showInputDialog(this, "Enter a name for the state:");
        if (stateName != null && !stateName.trim().isEmpty()) {//agar state name null nahi hai aur empty nahi hai tou yeh loop chalega
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stateName + ".dat"))) {
                oos.writeObject(rooms);
                JOptionPane.showMessageDialog(this, "State saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                statusBar.setText("State saved: " + stateName);
                isStateSaved = true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving state: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                statusBar.setText("Error saving state");
            }
        }
    }
    

    private void loadState() {
        File dir = new File(".");//yeh current directory ko represent krta hai
        File[] stateFiles = dir.listFiles((d, name) -> name.endsWith(".dat"));//yeh files ko filter krta hai jo .dat se end ho
        if (stateFiles != null && stateFiles.length > 0) {//agar files null nahi hai aur length greater than 0 hai tou yeh loop chalega
            String[] stateNames = new String[stateFiles.length];//yeh array banata hai jis me hum state names store krte hain
            for (int i = 0; i < stateFiles.length; i++) {//yeh loop chalega aur state names store krega
                stateNames[i] = stateFiles[i].getName().replace(".dat", "");//yeh .dat ko remove krta hai
            }
            String selectedState = (String) JOptionPane.showInputDialog(this, "Select a state to load:", "Load State", JOptionPane.QUESTION_MESSAGE, null, stateNames, stateNames[0]);

            if (selectedState != null) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedState + ".dat"))) {//yeh file read krta hai
                    rooms = (ArrayList<Room>) ois.readObject();
                    updateRoomTabs();//yeh room tabs ko update krta hai
                    JOptionPane.showMessageDialog(this, "State loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    statusBar.setText("State loaded: " + selectedState);
                    isStateSaved = true;
                } catch (IOException | ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(this, "Error loading state: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    statusBar.setText("Error loading state");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No saved states found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        //invokelater isliye use hota hai takay humare components properly initialize ho
        SwingUtilities.invokeLater(SmartHomeController::new);//yeh SmartHomeController kay constructor ko call krta hai
    }
}