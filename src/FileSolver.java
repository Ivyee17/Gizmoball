import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.rmi.UnexpectedException;
import java.util.List;

public class FileSolver {
    private AnimationWindow animationWindow;
    public FileSolver (AnimationWindow animationWindow){
        this.animationWindow=animationWindow;
    }

    public void removeAll() {

        Rectangle r=animationWindow.acquireOldBoundingBox();
        animationWindow.removeAll();
        animationWindow.buildingModeRepaint(r);
    }


    public void save() {
        JFileChooser jfc=new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("gizmofile(*.gizmo)", "gizmo");
        jfc.setFileFilter(filter);
        int option=jfc.showSaveDialog(animationWindow);

        if(option==JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();

            String fname = jfc.getName(file);
            if(fname.indexOf(".gizmo")==-1) {
                file = new File(jfc.getCurrentDirectory(), fname + ".gizmo");
            }
            List<AbstractGizmo> list = animationWindow.getGizmoList();
            try (FileWriter fw = new FileWriter(file)) {
                BufferedWriter bw = new BufferedWriter(fw);
                for (AbstractGizmo abstractGizmo : list) {
                    bw.write("x=" + abstractGizmo.x + " y=" + abstractGizmo.y
                            + " r=" + abstractGizmo.r + " crashMove=" + abstractGizmo.crashMove + " degree=" + abstractGizmo.degree
                            + " class=" + abstractGizmo.getClass().getName());
                    bw.newLine();
                    bw.flush();
                }
                fw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            file.setReadOnly();
        }
        else{
            JOptionPane.showMessageDialog(animationWindow,"not save");
        }
    }

    public void read() {
        removeAll();
        JFileChooser jfc=new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("gizmofile(*.gizmo)", "gizmo");
        jfc.setFileFilter(filter);
        jfc.showOpenDialog(animationWindow);
        File file=jfc.getSelectedFile();
        if(file.canWrite()==true){
            JOptionPane.showMessageDialog(animationWindow,"this file is broken, cannot read it");
            return;
        }
        removeAll();
        try(FileReader fr=new FileReader(file)){
            // check this file
            BufferedReader br=new BufferedReader(fr);
            String line;
            while((line=br.readLine())!=null){
                String[] splitStr=line.split(" ");
                int x=0,y=0,r=0,degree=0;
                boolean crashMove=false;
                Class t=Class.forName("AbstractGizmo");
                for(String str:splitStr){
                    if(str.startsWith("x=")) x=Integer.parseInt(splitStr[0].substring(2));
                    if(str.startsWith("y=")) y=Integer.parseInt(splitStr[1].substring(2));
                    if(str.startsWith("r=")) r=Integer.parseInt(splitStr[2].substring(2));
                    if(str.startsWith("crashMove=")) crashMove=Boolean.parseBoolean(splitStr[3].substring(10));
                    if(str.startsWith("degree=")) degree=Integer.parseInt(splitStr[4].substring(7));
                    if(str.startsWith("class="))  t=Class.forName( splitStr[5].substring(6));
                    //System.out.println(x+" "+y+r+crashMove+degree+t);
                }
                if(t==AbstractGizmo.class) throw new UnexpectedException("check!");
                animationWindow.addGizmo(x,y,r,degree,crashMove,t);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
