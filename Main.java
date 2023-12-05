public class Main {
    public static void main(String[] args) {
        Presentation presentation = new Presentation();
        presentation.generateTitles();
        presentation.generateSlideData();
        presentation.buildSlideShow();
    }
}