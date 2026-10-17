package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.EntregaModel;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class CadastroEntregasService {

    //Variavel responsável por coletar dados da classe EntregaModel
    EntregaModel entregaModel= new EntregaModel();

    //Variavel responsável por serializar ou deserializar objetos
    public Gson gson = new GsonBuilder().
            excludeFieldsWithoutExposeAnnotation(). //garante que apenas os campos @Expose sejam incluído no JSON
            create();


    //Variavel responsável por guardar as requisições http
    public Response response;

    //Variavel responsável por armazenar a URL podendo ser local ou do servidor
    //String baseUrl = "http://localhost:8080";
    String baseUrl = "https://tds-on-api-entregas-accb20374610.herokuapp.com";

    //Variavel responsável por representar o ID no método de deleção
    String idDelivery;

    String schemaPath = "src/main/resources/schemas/";

    JSONObject jsonSchema;

    //
    private final ObjectMapper mapper = new ObjectMapper();

    //Método reponsável por criar um Json
    public void setFieldsDelivery(String field, String value) {
        //Montando a lógica conforme os campos recebidos
        switch (field) {
            case "numeroPedido" -> entregaModel.setNumeroPedido(Integer.parseInt(value));
            case "nomeEntregador" -> entregaModel.setNomeEntregador(value);
            case "statusEntrega" -> entregaModel.setStatusEntrega(value);
            case "dataEntrega" -> entregaModel.setDataEntrega(value);
            default -> throw new IllegalStateException("Unexpected field" + field);
        }
    }


    //Método reponsável por criar a requisição
    public void createDelivery(String endPoint) {
        //Concatena a URL base com o end point recebido
        String url = baseUrl + endPoint;
        String bodyToSend = gson.toJson(entregaModel);
        response = given() //Configura a requisição
                .contentType(ContentType.JSON) //Indica o tipo de conteúdo da requisição JSON
                .accept(ContentType.JSON)
                .body(bodyToSend) //Define o corpo da requisição String JSON
                .when() //Define qual ação será realizada (GET, POST, PUT, DELETE)
                .post(url) //Envio da requsição mais a URL
                .then() //Define as etapas da validação da resposta
                .extract() //Extrai a resposta da requisição
                .response(); // Retorna o objeto Response que contem a resposta.
    }

    public void retrieveId (){
        idDelivery = String.valueOf(gson
                .fromJson(
                        response
                                .jsonPath()
                                .prettify(),
                        EntregaModel.class)
                .getNumeroPedido());
    }

    public void deleteDelivery(String endPoint) {
        String url = String.format("%s%s/%s", baseUrl, endPoint, idDelivery);
        response = given()
                .accept(ContentType.JSON)
                .when()
                .delete(url)
                .then()
                .extract()
                .response();
    }

    //Novo método para armazenar o schema em uma variável
    private JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            JSONTokener tokener = new JSONTokener(inputStream);
            return new JSONObject(tokener);
        }
    }

    public void setContract(String contract) throws IOException {
        switch (contract) {
            case "Cadastro bem-sucedido de entrega" -> jsonSchema = loadJsonFromFile(
                    schemaPath + "cadastro-bem-sucedido-de-entrega.json");
            default -> throw new IllegalStateException("Contrato inesperado"  + contract);
        }
    }

    public Set<ValidationMessage> validateResponseAgainstSchema() throws IOException {

        //Obter o corpo da resposta como String e converter para JSONObject
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        //Configurar o JsonSchemaFactory e criar JsonSchema
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = schemaFactory.getSchema(jsonSchema.toString());
        //Converter JSON de resposta para JsonNode
        JsonNode jsonResponseNode = mapper.readTree(jsonResponse.toString());
        //Validar o JSON de resposta contra o esquema
        Set<ValidationMessage> schemaValidationErrors = schema.validate(jsonResponseNode);

        return schemaValidationErrors;
    }
}
