package domino;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;

//Declara a classe pública PartidaDAO, responsável 
//pelas operações de persistência
//(inserção, busca, atualização) de objetos Partida no MongoDB.
//• Implements AutoCloseable permite usar try-with-resources 
//para garantir fechamento da conexão.
public class PartidaDAO implements AutoCloseable {
	
	private final MongoClient mongo;
	private final MongoCollection<Document> colPartidas;
	
	public PartidaDAO() {
		
		mongo = MongoClients.create("mongodb://localhost:27017");
		
		MongoDatabase db = mongo.getDatabase("domino_db");
		
		colPartidas = db.getCollection("partidas");
		
	}
	
	public void salvarOuAtualizar(Partida p) {
		
		if (p.id == null) p.id = new ObjectId();
		
		colPartidas.replaceOne(
				eq("_id", p.id),
				p.toDocument(),
				new com.mongodb.client.model.ReplaceOptions().upsert(true));
		
	}
	
	public Partida buscar(ObjectId id) {
		
		Document d = colPartidas.find(eq("_id", id)).first();
		
		return d == null ? null : Partida.fromDocument(d);
		
	}
	
	@Override
	public void close() {mongo.close();}

}
