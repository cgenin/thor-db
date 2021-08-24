package fr.genin.christophe.thor.core.actions;

import fr.genin.christophe.thor.core.actions.operations.*;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ThorOperations {
    public static final String $_NKEYIN = "$nkeyin";
    public static final String $_DEFINEDIN = "$definedin";
    public static final String $_CONTAINS_ANY = "$containsAny";
    public static final String $_CONTAINS = "$contains";
    public static final String $_IN_SET = "$inSet";
    public static final String $_IN = "$in";
    public static final String $_ELEM_MATCH = "$elemMatch";
    public static final String $_BETWEEN = "$between";
    public static final String $_NE = "$ne";
    public static final String $_LT = "$lt";
    public static final String $_TYPE = "$type";
    public static final String $_GTE = "$gte";
    public static final String $_FINITE = "$finite";
    public static final String $_SIZE = "$size";
    public static final String $_EXISTS = "$exists";
    public static final String $_OR = "$or";
    public static final String $_AND = "$and";
    public static final String $_NOT = "$not";
    public static final String $_GT = "$gt";
    public static final String $_LTE = "$lte";
    public static final String $_DTEQ = "$dteq";
    public static final String $_AEQ = "$aeq";
    public static final String $_EQ = "$eq";
    public static final String $_KEYIN = "$keyin";
    public static final String $_UNDEFINEDIN = "$undefinedin";
    public static final String $_CONTAINS_STRING = "$containsString";
    public static final String $_NIN = "$nin";
    private final static Logger LOG = LoggerFactory.getLogger(ThorOperations.class);

    private final static List<Operation> OPERATIONS = List.of(
            new Keyin(),
            new Nkeyin(),
            new Definedin(),
            new Eq(),
            new Aeq(),
            new And(),
            new Between(),
            new Contains(),
            new ContainsAny(),
            new ContainsString(),
            new Dteq(),
            new ElemMatch(),
            new Exists(),
            new Finite(),
            new Gt(),
            new Gte(),
            new In(),
            new InSet(),
            new Lt(),
            new Lte(),
            new Ne(),
            new Nin(),
            new Not(),
            new Or(),
            new Size(),
            new Type(),
            new Undefinedin()
    );


    public static BiFunction<Object, Object, Boolean> run(String function) {

        return OPERATIONS.find(op -> op.getName().equals(function))
                .getOrElse(() -> {
                    LOG.error("Method not found in ThorOps " + function);
                    return new Operation("unknown") {
                        @Override
                        public Boolean apply(Object o, Object o2) {
                            return false;
                        }
                    };
                })

                ;
    }

    public static class JsonPredicate implements Predicate<Object> {
        private final Object obj;

        public JsonPredicate(Object obj) {
            this.obj = obj;
        }

        @Override
        public boolean test(Object o) {
            if (obj instanceof JsonObject) {
                return Commons.doQueryOp(o, (JsonObject) obj);
            }
            return false;
        }
    }
}
