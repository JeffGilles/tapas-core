package mcib3d.tapas.plugins;

import com.sun.tools.javah.Util;
import ij.IJ;
import ij.WindowManager;
import ij.plugin.BrowserLauncher;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.core.TapasDocumentation;
import mcib3d.tapas.core.TapasProcessingAbstract;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

        //Collections.sort(pluginsName);

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

        for (String key : docCategories.keySet()) {
            //IJ.log(""+key);
//            if (key.contains("Input")){
////                List<String> namesPlugins = retrieveNames(docCategories.get(key), plugins);
////                IJ.log(""+namesPlugins.size());
////                for( String key2 : namesPlugins){
////                    comboBox1.addItem(key2);
////                    IJ.log(key2);
////                }
//                IJ.log("docSize="+docCategories.get(key).size());
//                for (int i = 0; i < docCategories.get(key).size(); i++){
//                    String fullName = docCategories.get(key).get(i);
//                    //get Key from value (value = fullName)
//                    IJ.log(fullName);
//                    for(String key2 : plugins.keySet()){
//                        IJ.log(">>"+key2);
//                        //if plugins value for the current key matches, return the key and add it in combobox
//                        if( plugins.get(key2).equals(fullName) ){
//                            IJ.log("_____________"+key2);
//                            comboBox1.addItem(key2);
//                        }
//                    }
//                }
//                IJ.log("     "+key);
//
//            }

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
        // fill the combo

        //for (String key : pluginsName) {
        //    comboBox1.addItem(key);
        //}
//        for (int keyNb=0; keyNb< pluginsName.size(); keyNb++){
////            if (keyNb <=13)comboBox1.addItem(pluginsName.get(keyNb));
//            if (keyNb >13 && keyNb <=16)comboBox2.addItem(pluginsName.get(keyNb));
//            if (keyNb >16 && keyNb <=26)comboBox3.addItem(pluginsName.get(keyNb));
//            if (keyNb >26 && keyNb <=33)comboBox4.addItem(pluginsName.get(keyNb));
//            if (keyNb >33 && keyNb <=53)comboBox5.addItem(pluginsName.get(keyNb));
//            if (keyNb >53 && keyNb <pluginsName.size())comboBox6.addItem(pluginsName.get(keyNb));
//        }

        // display the frame
        //panel1.setMinimumSize(new Dimension(800, 600));
        //panel1.setPreferredSize(new Dimension(800, 600));
        setContentPane(panel1);
        setTitle("TAPAS MENU " + TapasBatchProcess.version);
        pack();
        setVisible(true);

        // register with Image
        WindowManager.addWindow(this);
        WindowManager.setWindow(this);

        comboBox1.setSelectedIndex(0);
        setSelectedPlugin(comboBox1.getSelectedItem().toString());
        //selectPlugins(comboBox1.getSelectedItem().toString());

        initText();

        //comboBox1.addActionListener(e -> selectPlugins(comboBox1.getSelectedItem().toString()));
        comboBox1.addActionListener(this);
//        comboBox1.setRenderer(new ComboSelectRenderer(comboBox1.getRenderer()));
        comboBox2.addActionListener(this);
        comboBox3.addActionListener(this);
        comboBox4.addActionListener(this);
        comboBox5.addActionListener(this);
        comboBox6.addActionListener(this);

        //comboBox3.addActionListener(e -> selectPlugins());
        generateTextButton.addActionListener(e -> createText(selectedPlugin));
        documentationButton.addActionListener(e -> getDocumentation());
        websiteButton.addActionListener(e -> launchWebsite());
        exportButton.addActionListener(e -> exportToText());
    }

    private void exportToText(){
        String dir = IJ.getDirectory("Select saving folder for the .txt file");

        try {
            File myObj = new File("TAPAS.txt");
            if (myObj.createNewFile()) {
                IJ.showMessage("File created: " + myObj.getName());
                //textArea1.getText();
            } else {
                IJ.showMessage("File already exists.");
            }
        } catch (IOException e) {
            IJ.showMessage("An error occurred.");
            e.printStackTrace();
        }

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
            //IJ.log(""+((JComboBox)e.getSource()).isFontSet());
//            if(((JComboBox)e.getSource()).isFontSet()==false){
//
//                ((JComboBox)e.getSource()).setForeground(Color.BLACK);
//            }
//            else{
//                ((JComboBox)e.getSource()).setForeground(Color.RED);
//            }
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

    private void selectPlugins(String plugin) {
        //String plugin = comboBox1.getSelectedItem().toString();
        String className = plugins.get(plugin);
        //IJ.log("Selected " + plugin + " " + className);
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
                String par = currentTapas.getParameter(parameters[i]);
                if ((par != null) && (!par.isEmpty())) paramsText[i].setText(par);
                else paramsText[i].setText("");
            }
            for (int i = np; i < maxParam; i++) {
                paramsLabel[i].setText("");
                paramsLabel[i].setVisible(false);
                paramsText[i].setEnabled(false);
                paramsText[i].setText("");
            }
        } catch (ClassNotFoundException e) {
            IJ.log("No class " + className);
        } catch (IllegalAccessException e) {
            IJ.log("Pb class " + className);
        } catch (InstantiationException e) {
            IJ.log("Pb init " + className);
        }
    }

    private void createUIComponents() {
    }


//    public void actionPerformed(ActionEvent e) {
//
//    }
}

//class ComboSelectRenderer extends DefaultListCellRenderer {
//
//    private ListCellRenderer defaultRenderer;
//
//    public ComboSelectRenderer(ListCellRenderer defaultRenderer) {
//        this.defaultRenderer = defaultRenderer;
//    }
//
//    @Override
//    public Component getListCellRendererComponent(JList list, Object value,
//                                                  int index, boolean isSelected, boolean cellHasFocus) {
//        Component c = defaultRenderer.getListCellRendererComponent(list, value,
//                index, isSelected, cellHasFocus);
//        if (c instanceof JLabel) {
//            if (isSelected) {
//                c.setForeground(Color.GREEN);
//            } else {
//                c.setForeground(Color.red);
//            }
//        } else {
//            c.setBackground(Color.white);
//            c = super.getListCellRendererComponent(list, value, index, isSelected,
//                    cellHasFocus);
//        }
//        return c;
//    }
//}