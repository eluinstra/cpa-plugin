/**
 * Copyright 2011 Clockwork
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
 * QCpaTemplate is a Querydsl query type for QCpaTemplate
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QCpaTemplate extends com.querydsl.sql.RelationalPathBase<QCpaTemplate> {

    private static final long serialVersionUID = 1655323765;

    public static final QCpaTemplate cpaTemplate = new QCpaTemplate("cpa_template");

    public final StringPath content =	createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QCpaTemplate(String variable) {
        super(QCpaTemplate.class, forVariable(variable), "PUBLIC", "cpa_template");
        addMetadata();
    }

    public QCpaTemplate(String variable, String schema, String table) {
        super(QCpaTemplate.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCpaTemplate(String variable, String schema) {
        super(QCpaTemplate.class, forVariable(variable), schema, "cpa_template");
        addMetadata();
    }

    public QCpaTemplate(Path<? extends QCpaTemplate> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "cpa_template");
        addMetadata();
    }

    public QCpaTemplate(PathMetadata metadata) {
        super(QCpaTemplate.class, metadata, "PUBLIC", "cpa_template");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(content, ColumnMetadata.named("content").withIndex(2).ofType(Types.CLOB).withSize(1073741824).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(32).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(1).ofType(Types.VARCHAR).withSize(256).notNull());
    }

}

