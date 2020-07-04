/**
 * Copyright 2016 Ordina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.plugin.cpa.querydsl.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;

import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QCpaElement is a Querydsl query type for QCpaElement
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QCpaElement extends com.querydsl.sql.RelationalPathBase<QCpaElement> {

    private static final long serialVersionUID = 1655323765;

    public static final QCpaElement cpaElement = new QCpaElement("cpa_element");

    public final NumberPath<Long> cpaTemplateId = createNumber("cpa_template_id", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> orderNr = createNumber("order_nr", Long.class);

    public final StringPath xpathQuery =	createString("xpath_query");

    public QCpaElement(String variable) {
        super(QCpaElement.class, forVariable(variable), "PUBLIC", "cpa_element");
        addMetadata();
    }

    public QCpaElement(String variable, String schema, String table) {
        super(QCpaElement.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCpaElement(String variable, String schema) {
        super(QCpaElement.class, forVariable(variable), schema, "cpa_element");
        addMetadata();
    }

    public QCpaElement(Path<? extends QCpaElement> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "cpa_element");
        addMetadata();
    }

    public QCpaElement(PathMetadata metadata) {
        super(QCpaElement.class, metadata, "PUBLIC", "cpa_element");
        addMetadata();
    }

    public void addMetadata() {
      	addMetadata(cpaTemplateId, ColumnMetadata.named("cpa_template_id").withIndex(1).ofType(Types.BIGINT).withSize(32).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(32).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(1).ofType(Types.VARCHAR).withSize(256).notNull());
        addMetadata(orderNr, ColumnMetadata.named("order_nr").withIndex(1).ofType(Types.BIGINT).withSize(32).notNull());
        addMetadata(xpathQuery, ColumnMetadata.named("xpath_query").withIndex(2).ofType(Types.CLOB).withSize(256).notNull());
    }

}

