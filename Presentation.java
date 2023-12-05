import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xslf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.*;

public class Presentation {
    private Map<String, ArrayList<String>> slide_data = new HashMap<>();
    private String topic;
    private String audience;
    private String starting_content;
    private String slide_count;
    private ArrayList<String> slide_array = new ArrayList<>();
    private ArrayList<String> data = new ArrayList<>();
    public Presentation(){
        ObjectMapper mapper = new ObjectMapper();
        topic = "";
        audience = "";
        starting_content = "";
        slide_count = "";
        try {
            Map<String, String> data = mapper.readValue(new File("./src/presentation.json"), Map.class);
            topic = data.get("topic");
            audience = data.get("audience");
            starting_content = data.get("starting_content");
            slide_count = data.get("slide_count");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateTitles(){
        String slides = "";
        try {
            if(starting_content.equalsIgnoreCase("null")) {
                slides = OpenAI.chatGPT("Please come up with " + slide_count + " slide titles for a presentation on " + topic + " for people aged " + audience + "format the file as a json. Do not put slide numbers in the value for each slide.");
            } else if(!starting_content.equalsIgnoreCase("null")){
                slides = OpenAI.chatGPT("Please come up with " + slide_count + " slide titles for a presentation on " + topic + " for people aged " + audience +" format the file as a json. Do not put slide numbers in the value for each slide. Base your titles on the topics covered in the following infomation " + starting_content );
            }
            FileWriter jsonSlides = new FileWriter("./jsonSlides.json");
            jsonSlides.write(slides);
            jsonSlides.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> slide_title_map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            slide_title_map = mapper.readValue(new File("./jsonSlides.json"), Map.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        for (Map.Entry<String, String> entry : slide_title_map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        for(int i = 1; i < slide_title_map.size() + 1; i++){
                slide_array.add(slide_title_map.get("slide" + i));
        }
        System.out.println(slide_array);
        slide_array = arrayCleanup(slide_array);
    }

    public void generateSlideData(){
        String presentationJson = "";
        ObjectMapper mapper = new ObjectMapper();
        for(int i = 0; i < slide_array.size(); i++) {
            try {
                if(starting_content.equalsIgnoreCase("null")){
                    String slide_content = OpenAI.chatGPT("Please come up with 2 or 3 bullet points for a slide titled " + slide_array.get(i) + " for a presentation on " + topic + " for people aged " + audience + ". write the file as a json. Keep the bullet points short. It should be easy to read during an oral presentation. The json file should be structured as a dictionary with the slide title as the key and the bullet points as the value. The bullet points should be in a list.");
                } else if(!starting_content.equalsIgnoreCase("null")){
                    String slide_content = OpenAI.chatGPT("Please come up with 2 or 3 bullet points for a slide titled " + slide_array.get(i) + " for a presentation on " + topic + " for people aged " + audience + ". write the file as a json. Keep the bullet points short. It should be easy to read during an oral presentation. The json file should be structured as a dictionary with the slide title as the key and the bullet points as the value. The bullet points should be in a list. Base your bullet points off of this information: " + starting_content);
                }
                String slide_content = OpenAI.chatGPT("Please come up with 2 or 3 bullet points for a slide titled " + slide_array.get(i) + " for a presentation on " + topic + " for people aged " + audience + ". write the file as a json. Keep the bullet points short. It should be easy to read during an oral presentation. The json file should be structured as a dictionary with the slide title as the key and the bullet points as the value. The bullet points should be in a list.");
                FileWriter jsonSlideContent = new FileWriter("./jsonSlideContent.json");
                jsonSlideContent.write(slide_content);
                jsonSlideContent.close();
                Map<String, ArrayList<String>> temp = new HashMap<>();
                try{
                    temp = mapper.readValue(new File("./jsonSlideContent.json"), Map.class);
                    slide_data.put(slide_array.get(i), temp.get(slide_array.get(i)));
                } catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, ArrayList<String>> entry : slide_data.entrySet()) {
            System.out.println("Key : " + entry.getKey());
            System.out.println("Value : " + entry.getValue());
            System.out.println("-------------------");
        }
    }

    public void buildSlideShow(){
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
        XSLFSlide slide = ppt.createSlide(layout);

        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                XSLFTextShape textShape = (XSLFTextShape) shape;
                textShape.clearText();
            }
        }

        XSLFTextShape titleShape = slide.getPlaceholder(0);
        XSLFTextShape contentShape = slide.getPlaceholder(1);
        titleShape.setText(topic + " For People Aged " + audience);

        for (int i = 0; i < slide_data.size(); i++) {
            slide = ppt.createSlide(layout);

            // Clear the existing text in title and content placeholders for the new slide
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    textShape.clearText();
                }
            }

            titleShape = slide.getPlaceholder(0);
            titleShape.setText(slide_array.get(i));

            contentShape = slide.getPlaceholder(1);
            for (int j = 0; j < slide_data.get(slide_array.get(i)).size(); j++) {
                contentShape.addNewTextParagraph().addNewTextRun().setText(slide_data.get(slide_array.get(i)).get(j));
            }
        }

        try {
            FileOutputStream out = new FileOutputStream("powerpoint.pptx");
            ppt.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> arrayCleanup(ArrayList<String> a){
       for(int i = 0; i < a.size(); i++){
           if(a.get(i) == null){
               a.remove(i);
           }
       }
       return a;
    }

    public String getTopic(){
        return this.topic;
    }

    public String getSlideCount(){
        return this.slide_count;
    }

    public String getAudience(){
        return this.audience;
    }

    public String getStartingContent(){
        return this.starting_content;
    }

    public Map<String, ArrayList<String>> getSlideData(){
        return this.slide_data;
    }
}
