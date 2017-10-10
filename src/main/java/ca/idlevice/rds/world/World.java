package ca.idlevice.rds.world;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@RequiredArgsConstructor
@Document
@Getter
@Setter
public class World {

    private @Id
    //ObjectId _id;
    String id;
    private final String worldName;
    private final int age;
}
