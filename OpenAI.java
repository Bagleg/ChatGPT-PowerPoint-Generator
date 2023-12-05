import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAI {
    private static final String apiKey = System.getenv("OPENAI_API_KEY");

    public OpenAI() {
    }

    public static String chatGPT(String prompt) throws IOException {
        String url = "https://api.openai.com/v1/chat/completions";
        String model = "gpt-4";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt.replace("\n", "\\n") + "\"}]}";
            System.out.println(body);
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();

            String line;
            while((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.toString());
            System.out.println(node);
            System.out.println(node.get("choices").get(0).get("message").get("content").asText());
            return node.get("choices").get(0).get("message").get("content").asText();
        } catch (IOException var12) {
            throw new RuntimeException(var12);
        }
    }
}
