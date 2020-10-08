package mcib3d.tapas.plugins;

import ij.IJ;
import ij.WindowManager;
import ij.plugin.frame.Recorder;
import mcib3d.tapas.IJ.TapasProcessorIJ;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.core.TapasProcessorAbstract;
import omero.gateway.model.DatasetData;
import omero.gateway.model.ImageData;
import omero.gateway.model.ProjectData;
import weka.core.pmml.jaxbbindings.False;
import weka.core.pmml.jaxbbindings.True;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TAPAS_FILES2 extends JFrame {
    private JPanel panel1;
    private JPanel pan1;
    private JPanel pan2;
    private JList listImages;
    private JPanel selectPan;
    private JRadioButton OMERORadioButton;
    private JRadioButton localRadioButton;
    private JPanel foldersPan;
    private JTextField textFieldRoot;
    private JComboBox comboDatasets;
    private JTextField textFieldImage;
    private JTextField textFieldProcess;
    private JButton browseButtonRoot;
    private JButton browseButtonProcess;
    private JComboBox comboProjects;
    private JButton runProcessingButton;
    private JCheckBox jobsCheckBox;

    String rootProject;
    OmeroConnect omero;
    DefaultListModel model = new DefaultListModel();
    File tapasFile;
    // processor
    TapasProcessorAbstract processor;

    public TapasProcessorAbstract getProcessor() {
        return processor;
    }

    public void setProcessor(TapasProcessorAbstract processorAbstract) {
        this.processor = processorAbstract;
    }

    public TAPAS_FILES2() {
        // FIXME will be deprecated in 0.7, will be in tapas folder with name .tpm
        tapasFile = TapasBatchUtils.getTapasMenuFile();
        if (tapasFile == null) {
            return;
        }
        listImages.setModel(model);
        //textFieldFrame.setText("0-0");
        //textFieldChannel.setText("0-0");
        panel1.setMinimumSize(new Dimension(800, 350));
        panel1.setPreferredSize(new Dimension(800, 400));
        setContentPane(panel1);
        setTitle("TAPAS FILES " + TapasBatchProcess.version);
        pack();
        setVisible(true);

        // register with Image
        WindowManager.addWindow(this);
        WindowManager.setWindow(this);

        // fill projects
//        omero = new OmeroConnect();
//        try {
//            omero.connect();
//            OMERORadioButton.setSelected(true);
//            // projects
//            List<ProjectData> projects = omero.findAllProjects();
//            projects.sort(new TAPAS_FILES2.compareProject());
//            for (int i = 0; i < projects.size(); i++) {
//                comboProjects.addItem(projects.get(i).getName());
//            }
//            omero.disconnect();
//            selectProject();
//            localRadioButton.setSelected(false);
//        } catch (Exception e1) {
//            localRadioButton.setSelected(true);
//            OMERORadioButton.setSelected(false);
//            e1.printStackTrace();
//        }

        OMERORadioButton.addActionListener(e -> testOMERO());
        localRadioButton.addActionListener(e -> testOMERO());
        browseButtonRoot.addActionListener(e -> browseRoot());
        comboProjects.addActionListener(e -> selectProject());
        comboDatasets.addActionListener(e -> selectDataset());
        listImages.addListSelectionListener(e -> selectimage());
        runProcessingButton.addActionListener(e -> {
            runProcessingButton.setEnabled(false);
            processing();
        });
        browseButtonProcess.addActionListener(e -> browseProcess());
    }

    private void processing() {
        // batch process
        TapasBatchProcess batchProcess = new TapasBatchProcess();
        String project = comboProjects.getSelectedItem().toString();//textFieldProject.getText();
        String dataset = comboDatasets.getSelectedItem().toString();//textFieldDataset.getText();
        // images
        String image = "";
        int[] indices = listImages.getSelectedIndices();
        if (indices.length == 1) image = textFieldImage.getText(); // by default selected image
        if (indices.length == model.getSize()) image = "*";
        if (indices.length == 0) {
            String rep = IJ.getString("Process all files (y/n) ?", "n");
            if (rep.equalsIgnoreCase("y")) image = "*";
            else {
                runProcessingButton.setEnabled(true);
                return;
            }
        }
        if ((indices.length > 1) && (indices.length < model.size())) {
            image = "";
            for (int i = 0; i < indices.length - 1; i++) {
                image = image.concat(model.get(indices[i]).toString() + ",");
            }
            image = image.concat(model.get(indices[indices.length - 1]).toString());
        }
        String imageFinal = image;

        String processFile = textFieldProcess.getText();
        if (!batchProcess.init(processFile, tapasFile.getAbsolutePath())) {
            IJ.log("Aborting");
            return;
        }
        // channel
        //String channel = textFieldChannel.getText();
        //int[] channels = processTextForTimeChannel(channel);
        int cmin = 1;
        int cmax = 1;
        // frame
        //String frame = textFieldFrame.getText();
        //int[] frames = processTextForTimeChannel(frame);
        int tmin = 1;
        int tmax = 1;
        // init to find images
        Thread thread = new Thread(() -> {
            batchProcess.setProcessor(new TapasProcessorIJ());
            batchProcess.initBatchFiles(rootProject, project, dataset, imageFinal, cmin, cmax, tmin, tmax);
            batchProcess.processAllImages();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> {
                // Macro
                if (Recorder.record) {
                    Recorder.setCommand(null);
                    Recorder.record("run", "TAPAS BATCH", "root=[" + rootProject + "] project=[" + project + "] dataset=[" + dataset + "] image=[" + imageFinal
                            + "] channel=[" + cmin + "-" + cmax + "] frame=[" + tmin + "-" + tmax + "] processing=[" + processFile + "]");
                }
                IJ.log("Done");
                runProcessingButton.setEnabled(true);
            });
        });
        thread.start();
    }

    private void browseRoot() {
        browseButtonRoot.setEnabled(false);
        String dir = IJ.getDirectory("Select root folder for projects");
        if (dir == null) {
            browseButtonRoot.setEnabled(true);
            return;
        }
        textFieldRoot.setText(dir);
        rootProject = dir;
        IJ.log("Found root project : " + rootProject);
        // fill projects
        File folder = new File(rootProject);
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> files = new ArrayList(listOfFiles.length);
        comboProjects.removeAllItems();
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                files.add(file);
            }
        }
        Collections.sort(files, new TAPAS_FILES2.compareFile());
        for (File file : files) {
            comboProjects.addItem(file.getName());
        }
        //textFieldProject.setText(files.get(0).getName());
        browseButtonRoot.setEnabled(true);
    }

    private void selectProject() {
        if (comboProjects.getSelectedItem() == null) return;
        String project = comboProjects.getSelectedItem().toString();
        //textFieldProject.setText(project);
        // fill datasets
        if(OMERORadioButton.isSelected() == true){
            // fill datasets
            try {
                omero.connect();
                // project
                ProjectData projectData = omero.findProject(project, true);
                List<DatasetData> datasets = omero.findDatasets(projectData);
                datasets.sort(new compareDataset());
                comboDatasets.removeAllItems();
                for (int i = 0; i < datasets.size(); i++) {
                    comboDatasets.addItem(datasets.get(i).getName());
                }
                if (!datasets.isEmpty())
                    comboDatasets.setSelectedItem(datasets.get(0).getName());
                omero.disconnect();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        else {
            File folder = new File(rootProject + project);
            IJ.log("Selected project : " + folder);
            File[] listOfFiles = folder.listFiles();
            ArrayList<File> files = new ArrayList(listOfFiles.length);
            comboDatasets.removeAllItems();
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    files.add(file);
                }
            }
            Collections.sort(files, new compareFile());
            for (File file : files) {
                comboDatasets.addItem(file.getName());
            }
        }
        //if (!files.isEmpty()) textFieldDataset.setText(files.get(0).getName());
    }

    private void selectDataset() {
        String project = comboProjects.getSelectedItem().toString();
        if (comboDatasets.getSelectedItem() == null) return;
        String dataset = comboDatasets.getSelectedItem().toString();
        //textFieldDataset.setText(dataset);
        // fill images
        if(OMERORadioButton.isSelected() == true){
            // fill images
            try {
                omero.connect();
                // project
                ProjectData projectData = omero.findProject(project, true);
                DatasetData datasetData = omero.findDataset(dataset, projectData, true);
                List<ImageData> images = omero.findAllImages(datasetData);
                images.sort(new compareImages());
                model.removeAllElements();
                for (int i = 0; i < images.size(); i++) {
                    model.addElement(images.get(i).getName());
                }
                textFieldImage.setText("");
                listImages.updateUI();
                listImages.repaint();
                repaint();
                omero.disconnect();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        else {
            File folder = new File(rootProject + project + File.separator + dataset);
            IJ.log("Selected dataset : " + folder);
            File[] listOfFiles = folder.listFiles();
            ArrayList<File> files = new ArrayList(listOfFiles.length);
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    files.add(file);
                }
            }
            Collections.sort(files, new compareFile());
            model.removeAllElements();
            for (File file : files) {
                model.addElement(file.getName());
            }
        }
    }

    private void testOMERO() {
        if(OMERORadioButton.isSelected() == true){
            localRadioButton.setSelected(false);
            // fill projects
            omero = new OmeroConnect();
            try {

                omero.connect();
                OMERORadioButton.setSelected(true);
                // projects
                List<ProjectData> projects = omero.findAllProjects();
                projects.sort(new TAPAS_FILES2.compareProject());
                for (int i = 0; i < projects.size(); i++) {
                    comboProjects.addItem(projects.get(i).getName());
                }
                omero.disconnect();
                selectProject();
            } catch (Exception e1) {
                IJ.showMessage("You're not connected, go in TAPAS CONNECT first");
                OMERORadioButton.setSelected(false);
                localRadioButton.setSelected(true);
                //e1.printStackTrace();
            }
        }
        else {
            localRadioButton.setSelected(true);
        }
    }

    private void selectimage() {
        int[] indices = listImages.getSelectedIndices();
        String text;
        if (indices.length == 1) text = model.get(indices[0]).toString();
        else text = indices.length + " images selected";
        textFieldImage.setText(text);
    }

    private void browseProcess() {
        String file = IJ.getFilePath("Choose process");
        textFieldProcess.setText(file);
    }

    private int[] processTextForTimeChannel(String nextString) {
        int[] vals = new int[2];
        if (nextString.contains("-")) {
            String[] cs = nextString.split("-");
            vals[0] = Integer.parseInt(cs[0]);
            vals[1] = Integer.parseInt(cs[1]);
        } else {
            vals[0] = Integer.parseInt(nextString);
            vals[1] = vals[0];
        }
        return vals;
    }

    private class compareFile implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    private class compareProject implements Comparator<ProjectData> {
        @Override
        public int compare(ProjectData o1, ProjectData o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    class compareDataset implements Comparator<DatasetData> {
        @Override
        public int compare(DatasetData o1, DatasetData o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    class compareImages implements Comparator<ImageData> {
        @Override
        public int compare(ImageData o1, ImageData o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
}
