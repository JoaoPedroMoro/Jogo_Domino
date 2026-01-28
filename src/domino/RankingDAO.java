package domino;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class RankingDAO implements AutoCloseable {

	private static final MongoClient mongo = MongoClients.create("mongodb://localhost:27017");
	
	private static final MongoCollection<Document> col;
	
	static {
		
		MongoDatabase db = mongo.getDatabase("domino_db");
		
		col = db.getCollection("ranking");
		
	}
	
	public static void registrarResultado(String nome, boolean vitoria) {
		
		col.updateOne(
				eq("_id", nome),
				inc(vitoria ? "vitorias" : "derrotas", 1),
				new com.mongodb.client.model.UpdateOptions().upsert(true));
		
	}
	
	public static String topCinco() {
		
		StringBuilder sb = new StringBuilder("<html><h3>RANKING</h3>");
		
		col.find()
		   .sort(new Document("vitorias", -1))
		   .limit(5)
		   .forEach(doc -> {
			   
			   sb.append(doc.getString("_id"))
			     .append(": ")
			     .append(doc.getInteger("vitorias", 0))
			     .append(" vitória(s) - ")
			     .append(doc.getInteger("derrotas", 0))
			     .append(" derrota(s)<br>");
		   });
		
		sb.append("</html>");
		
		return sb.toString();
		
	}
	
	@Override
	public void close() {mongo.close();}
	
}
