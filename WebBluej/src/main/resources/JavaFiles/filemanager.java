import java.nio.file.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
class filemanager extends clearscreen
{
    Scanner sc=new Scanner(System.in);
    private Path rootdir;
    private char sep=92;
    private String in="";
    
    
    void startup()
    {
        //startpoint
        accesspartition();//displays all partition
        
        inputforfirstaccess();
    }
    
    
    public void accesspartition()
    {
        FileSystem partition=FileSystems.getDefault();
        
        try{
            for(FileStore directory:partition.getFileStores())
            {
                displaypartitiondetails(directory);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    void displaypartitiondetails(FileStore space)
    {
        System.out.println(space.toString());
        try{
           long totalSpace, totalAvailableSpace;
           totalSpace=space.getTotalSpace();
           totalAvailableSpace=space.getUsableSpace();
        
           System.out.println("Total Space Available :"+totalSpace);
           System.out.println("Total Available Space :"+totalAvailableSpace);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    
    }
    
    
    private void inputforfirstaccess()
    {
            
            String input = sc.nextLine();
            
            if(input.equals("Exit"))
            {
               System.exit(0); 
            }
            
            else
            {
                Path dir=Paths.get("",input.concat(":/"));
                
                accessfurther(dir,"");
            }
            
    }
    
    
    private void accessfurther(Path dir,String folder)
    {
         
        
        if(folder!="")
          rootdir=Paths.get(dir.toString(),"/",folder);
        else
          rootdir=Paths.get(dir.toString());
        
          try{
              if(!Files.exists(rootdir))
                System.out.println("Does not Exist");
              else
              {
                  clearscreen();
                  System.out.println(rootdir.toString());
                  
                  File Dir=new File(rootdir.toString());
                  File[] allfolder=Dir.listFiles();
                  
                  for(int i=0;i<allfolder.length;i++)
                  {
                     System.out.println(allfolder[i].getName());                     
                  }
              }
              input();
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
          
    }
    
    private void input() throws Exception
    {
               System.out.println("Press E to go back OR EXIT to exit.");
               in=sc.nextLine();
                if(in.equals("E"))
                {
                    goback(rootdir);
                }
                else if((in.substring(0,4)).equals("open"))
                {
                   // String filepath=;
                   openaction(rootdir.toString().concat(String.valueOf(sep)).concat(in.substring(5)));
                }
                else if(in.equals("Exit")||in.equals("exit"))
                {
                   System.exit(0);
                }
                else if((in.substring(0,2)).equals("c "))
                {
                    createfile();
                }
                else if((in.substring(0,5)).equals("cdir "))
                {
                    createdir();
                }
                else if((in.substring(0,2)).equals("d "))
                {
                    delete();
                }
                else
                {
                    accessfurther(rootdir,in);
                }
    }
    
    
    private void goback(Path del)
    {
        
        String rdel=del.toString(),newdel="";
        int len=rdel.length(),target=0;
        
        for(int i=len-1;i>0;i--)
        {
            if((int)rdel.charAt(i)==92){
                target=1;
                newdel=rdel.substring(0,i);
                break;
            }
        }
        
        if(target==1 && len!=3)
        {
            Path dir=Paths.get("",newdel);
            accessfurther(dir,"");
        }
        else{
            try{
                sc=null;
                clearscreen();
                System.gc();
                main in=new main();
                in.main();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    
    private void createfile() throws Exception
    {
        Path newfile=Paths.get(rootdir.toString(),in.substring(2));
        
        if(Files.exists(newfile))
        {
            System.out.println("File already Exists");
            input();
        }
        else
        {
           Files.createFile(newfile);
           accessfurther(rootdir,"");
        }
        
    }
    
    private void createdir() throws Exception
    {
        Path newdir=Paths.get(rootdir.toString(),in.substring(5));
        
        if(Files.exists(newdir))
        {
            System.out.println("Directory already Exists.");
            input();
        }
        else
        {
            Files.createDirectory(newdir);
            accessfurther(rootdir,"");
        }
    }
    /*
    private void delete() throws Exception
    {
        Path toDelete=Paths.get(rootdir.toString(),in.substring(2));
        
        if(!Files.exists(toDelete))
        {
            System.out.println("Doesnt Exists.");
            input();
        }
        else
        {
            Files.delete(toDelete);
            accessfurther(rootdir,"");
        } 
    }
    */
   
   
   private void delete() 
   {
       try{
           Path toDelete=Paths.get(rootdir.toString(),in.substring(2));
           Files.delete(toDelete);
           accessfurther(rootdir,"");
       }
        catch(Exception e)
        {
            System.out.println("Files Doesn't Exists");
            try{input();}catch (Exception E){}
        }
   }
    void openaction(String filepath)  
    {
        try{
           Desktop.getDesktop().open(new File(filepath));
           input();
        }
       catch(Exception e)
       {
           System.out.println("Such File does not Exists");
           try{input();}catch(Exception E){}
       }
    }
}