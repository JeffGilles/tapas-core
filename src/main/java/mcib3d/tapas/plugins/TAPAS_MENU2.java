package mcib3d.tapas.plugins;

import ij.IJ;
import ij.WindowManager;
import ij.io.SaveDialog;
import ij.plugin.BrowserLauncher;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.core.TapasDocumentation;
import mcib3d.tapas.core.TapasProcessingAbstract;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TAPAS_MENU2 extends JFrame implements ActionListener {
    private JPanel panel1;
    private JTextArea textArea1;
    private JPanel panel2;
    private JPanel panelLeft;
    private JPanel panelRight;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JComboBox comboBox4;
    private JComboBox comboBox5;
    private JComboBox comboBox6;
    private JTextPane descriptionTAPAS;
    private JButton documentationButton;
    private JButton websiteButton;
    private JTextField textField1;
    private JLabel paramLabel;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;
    private JButton generateTextButton;
    private JLabel param1;
    private JLabel param2;
    private JLabel param3;
    private JLabel param4;
    private JLabel param5;
    private JLabel param6;
    private JLabel param7;
    private JLabel param8;
    private JLabel param9;
    private JLabel param10;
    private JLabel textFieldDescription;
    private JButton exportButton;

    private String selectedPlugin;

    TapasDocumentation documentation;

    File tapasFile;
    HashMap<String, String> plugins;
    int maxParam = 10;
    JTextField paramsText[] = new JTextField[maxParam];
    JLabel paramsLabel[] = new JLabel[maxParam];
    TapasProcessingAbstract currentTapas;

    public TAPAS_MENU2() {
        // init parameters fields // FIXME dynamic
        paramsText[0] = textField1;
        paramsLabel[0] = param1;
        paramsText[1] = textField2;
        paramsLabel[1] = param2;
        paramsText[2] = textField3;
        paramsLabel[2] = param3;
        paramsText[3] = textField4;
        paramsLabel[3] = param4;
        paramsText[4] = textField5;
        paramsLabel[4] = param5;
        paramsText[5] = textField6;
        paramsLabel[5] = param6;
        paramsText[6] = textField7;
        paramsLabel[6] = param7;
        paramsText[7] = textField8;
        paramsLabel[7] = param8;
        paramsText[8] = textField9;
        paramsLabel[8] = param9;
        paramsText[9] = textField10;
        paramsLabel[9] = param10;

        // test
        descriptionTAPAS.setContentType("text/html");
        descriptionTAPAS.setText("Documentation will appear here.");

        // read list of Tapas
        tapasFile = TapasBatchUtils.getTapasMenuFile();
        if (tapasFile == null) return;
        plugins = TapasBatchProcess.readPluginsFile(tapasFile.getAbsolutePath(), false);
        ArrayList<String> pluginsName = new ArrayList<>(plugins.size());
        for (String key : plugins.keySet()) {
            pluginsName.add(key);
            //IJ.log("" + key+" "+plugins.get(key));
        }

        // documentation
        documentation = new TapasDocumentation();
        documentation.loadDocumentation(tapasFile.getParent() + File.separator + "tapasDocumentation.txt");

        HashMap<String, List<String>> docCategories = documentation.getCategories();
        comboBox1.removeAllItems();
        comboBox2.removeAllItems();
        comboBox3.removeAllItems();
        comboBox4.removeAllItems();
        comboBox5.removeAllItems();
        comboBox6.removeAllItems();

        // fill the combos
        for (String key : docCategories.keySet()) {
            if (key.contains("Input")) {
                List<String> namesPlugins = retrieveNames(docCategories.get(key), plugins);
                for (String key2 : namesPlugins) {
                    comboBox1.addItem(key2);
                }
            }
            if (key.contains("Calibration") || key.equals("processing")) {
                List<String> namesPlugins = retrieveNames(docCategories.get(key), plugins);
                for (String key2 : namesPlugins) {
                    comboBox2.addItem(key2);
                }
            }
            if (key.equals("thresholding") || key.equals("segmentation")) {
                List<String> namesPlugins = retrieveNames(docCategories.get(key), plugins);
                for (String key2 : namesPlugins) {
                    comboBox3.addItem(key2);
                }
            }
            if (key.equals("post-processing")) {
                List<String> namesPlugins = retrieveNames(docCategories.get(key), plugins);
                for (String key2 : namesPlugins) {
                    comboBox4.addItem(key2);
                }
            }
            if (key.equals("measurement") || key.equals("distances") || key.equals("analysis")) {
                List<String> namesPlugins = retrieveNames(docCategories.get(key), plugins);
                for (String key2 : namesPlugins) {
                    comboBox5.addItem(key2);
                }
            }
            if (key.equals("misc.") || key.equals("utils")) {
                List<String> namesPlugins = retrieveNames(docCategories.get(key), plugins);
                for (String key2 : namesPlugins) {
                    comboBox6.addItem(key2);
                }
            }
        }

        // display the frame
        setContentPane(panel1);
        setTitle("TAPAS MENU " + TapasBatchProcess.version);
        pack();
        setVisible(true);

        // register with Image
        WindowManager.addWindow(this);
        WindowManager.setWindow(this);

        comboBox1.setSelectedIndex(0);
        setSelectedPlugin(comboBox1.getSelectedItem().toString());
        selectPlugins(comboBox1.getSelectedItem().toString());
        initText();

        //comboBox1.addActionListener(e -> selectPlugins(comboBox1.getSelectedItem().toString()));
        comboBox1.addActionListener(this);
        comboBox2.addActionListener(this);
        comboBox3.addActionListener(this);
        comboBox4.addActionListener(this);
        comboBox5.addActionListener(this);
        comboBox6.addActionListener(this);

        generateTextButton.addActionListener(e -> createText(selectedPlugin));
        documentationButton.addActionListener(e -> getDocumentation());
        websiteButton.addActionListener(e -> launchWebsite());
        exportButton.addActionListener(e -> exportToText());
    }

    /**
     * Retrieve key from value between list and map
     * @param map1
     * @param map2
     * @return
     */
    private List<String> retrieveNames(List<String> map1, HashMap<String, String> map2){
        List<String> names = new ArrayList<>();
        for (String name : map1){
        //for (int i = 0; i < map1.size(); i++){
            //String name = map1.get(i);

            //get Key from value (value = name)
            for(String key : map2.keySet()){
                //if plugins value for the current key matches, return the key and add it in combobox
                if( map2.get(key).equals(name) ){
                    names.add(key);
                }
            }
        }
        return names;
//                for (int i = 0; i < docCategories.get(key).size(); i++){
//                    String fullName = docCategories.get(key).get(i);
//                    //get Key from value (value = fullName)
//                    for(String key2 : plugins.keySet()){
//                        //if plugins value for the current key matches, return the key and add it in combobox
//                        if( plugins.get(key2).equals(fullName) ){
//                            comboBox1.addItem(key2);
//                        }
//                    }
//                }
    }

    public void actionPerformed(ActionEvent e) {
        if( e.getSource() instanceof JComboBox) {
            String selected = ((JComboBox)e.getSource()).getSelectedItem().toString();
            selectPlugins(selected);
            //((JComboBox<?>) e.getSource()).setBorder(BorderFactory.createLineBorder(Color.RED));
        }
    }

    private void getDocumentation() {
        try {
            BrowserLauncher.openURL("https://www.dropbox.com/s/mzcp8lyqbsz8t73/TapasDescription0.6.3.pdf?dl=0");
        } catch (IOException e) {
            IJ.log("Cannot find documentation");
        }
    }

    private void launchWebsite() {
        try {
            BrowserLauncher.openURL("https://imagej.net/TAPAS");
        } catch (IOException e) {
            IJ.log("Cannot find website");
        }
    }

    private void initText() {
        String process = "";
        process = process.concat("// first process should be input \n");
        process = process.concat("// to read image from OMERO \n");
        process = process.concat("// or from file \n");
        process = process.concat("process:input \n");
        textArea1.append(process);
        textArea1.append("\n");
    }

    /**
     * Create text in the textArea corresponding to the plugin parameters
     * @param plugin
     */
    private void createText(String plugin) {
        String process = "";
        process = process.concat("// " + currentTapas.getName() + "\n");
        //process = process.concat("process:" + comboBox1.getSelectedItem().toString() + "\n");
        process = process.concat("process:" + plugin + "\n");
        // parameters
        String[] parameters = currentTapas.getParameters();
        int np = parameters.length;
        for (int i = 0; i < np; i++) {
            process = process.concat(parameters[i] + ":" + paramsText[i].getText() + "\n");
        }
        textArea1.append(process);
        textArea1.append("\n");

        // TEST Log
        documentation.printCategories();
    }

    private void setSelectedPlugin(String selected){
        selectedPlugin = selected;
    }

    /**
     * Adjust the different fields to the selected plugin
     * @param plugin
     */
    private void selectPlugins(String plugin) {
        String className = plugins.get(plugin);
        // create plugin
        Class cls;
        try {
            cls = Class.forName(className);
            Object object = cls.newInstance();
            currentTapas = (TapasProcessingAbstract) object;
            textFieldDescription.setText(currentTapas.getName());
            textFieldDescription.setHorizontalAlignment(SwingConstants.CENTER);
            // documentation
            String doc = documentation.getDocumentation(currentTapas.getClass().getName());
            descriptionTAPAS.setText(doc);

            // parameters
            String[] parameters = currentTapas.getParameters();
            int np = parameters.length;
            for (int i = 0; i < np; i++) {
                paramsLabel[i].setText(parameters[i]);
                paramsLabel[i].setVisible(true);
                paramsText[i].setEnabled(true);
                paramsText[i].setBackground(Color.white);
                String par = currentTapas.getParameter(parameters[i]);
                if ((par != null) && (!par.isEmpty())) paramsText[i].setText(par);
                else paramsText[i].setText("");
            }
            for (int i = np; i < maxParam; i++) {
                paramsLabel[i].setText("");
                paramsLabel[i].setVisible(false);
                paramsText[i].setEnabled(false);
                paramsText[i].setText("");
                paramsText[i].setBackground(new Color(200,200,206));
            }
        } catch (ClassNotFoundException e) {
            IJ.log("No class " + className);
        } catch (IllegalAccessException e) {
            IJ.log("Pb class " + className);
        } catch (InstantiationException e) {
            IJ.log("Pb init " + className);
        }
    }

    /**
     Create and write text file from the generated text
     */
    private void exportToText(){
        SaveDialog save = new SaveDialog("Save the generated .txt file", "TAPASfile", ".txt");
        String dir = save.getDirectory();
        String fileName = save.getFileName(); //with extension
        try {
            File file = new File(dir, fileName);
            if (file.createNewFile()) {
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(textArea1.getText());
                bw.close();
                IJ.showMessage("File created: " + file.getName());
            } else {
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(textArea1.getText());
                bw.close();
                IJ.showMessage("File already overwrited.");
            }
        } catch (IOException e) {
            IJ.showMessage("An error occurred.");
            e.printStackTrace();
        }
    }

//    private void createUIComponents() {
//    }
}