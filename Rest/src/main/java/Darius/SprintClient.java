package Darius;

import Darius.domain.Sprint;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.concurrent.Callable;

public class SprintClient {
    RestClient restClient = RestClient.builder()
            .requestInterceptor(new CustomRestSprintInterceptor())
            .build();

    public static final String URL = "http://localhost:8080/org/sprints";
    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) {
            throw new ServiceException(e.getMessage());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public Sprint[] getAll() {
        return execute(()->restClient.get().uri(URL).retrieve().body(Sprint[].class));
    }

    public Sprint getById(Integer id){
        return execute(()->restClient.get().uri(String.format("%s/%s", URL, id)).retrieve().body(Sprint.class));
    }

    public Sprint create(Sprint sprint){
        return execute(()->restClient.post().uri(URL).contentType(MediaType.APPLICATION_JSON).body(sprint).retrieve().body(Sprint.class));
    }

    public Sprint update(Integer id, Sprint sprint){
        return execute(()->restClient.put().uri(String.format("%s/%s", URL, id)).body(sprint).retrieve().body(Sprint.class));
    }

    public void delete(Integer id){
        execute(()->restClient.delete().uri(String.format("%s/%s", URL, id)).retrieve().toBodilessEntity());
    }

    public class CustomRestSprintInterceptor implements ClientHttpRequestInterceptor{

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            System.out.println("Sending a " + request.getMethod()+ " request to " + request.getURI() + " and body [" + new String(body) +"]");
            ClientHttpResponse response = null;
            try{
                response = execution.execute(request, body);
                System.out.println("Got response code " + response.getStatusCode());
            }catch (IOException ex){
                System.err.println("Eroare la executie "+ ex);
            }
            return response;
        }
    }
}
