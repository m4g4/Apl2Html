/**
 * @author m4g4
 * @date 24/04/2017
 */
public enum State {
    INITIAL(Grammar.NO_PATTERNS) {
        @Override
        public State getNewState(StateContext context, Grammar.LineMeta meta) {
            context.setModuleName(getAppName());
            return APPLICATION;
        }

        private String appName;

        @Override
        public String getAppName() {
            if (appName == null)
                throw new RuntimeException("App name not set!");
            return appName;
        }

        @Override
        public void setAppName(String appName) {
            this.appName = appName;
        }
    },
    APPLICATION(Grammar.Application.values()) {
        @Override
        public State getNewState (StateContext context, Grammar.LineMeta meta) {

            if (Grammar.Application.FUNCTION_DEFINITION.equals(meta.matchedPattern)) {
                context.setFnName(meta.groups.get(1));
                return FUNCTION_DEFINITION;

            } else if (Grammar.Application.CLASS.equals(meta.matchedPattern)) {
                context.setClName(meta.groups.get(2));
                return CLASS;

            } else if (Grammar.Application.COLLAPSE_ONLY.equals(meta.matchedPattern)) {
                return COLLAPSED;
            }

            return APPLICATION;
        }

        @Override
        public void closeScope(StateContext context) {
            context.setModuleName(null);
        }
    },

    CLASS(Grammar.Class.values()) {
        @Override
        public State getNewState (StateContext context, Grammar.LineMeta meta) {
            if (Grammar.Class.FUNCTION_DEFINITION.equals(meta.matchedPattern)) {
                context.setFnName(meta.groups.get(1));
                return FUNCTION_DEFINITION;

            } else if (Grammar.Class.VARIABLES.equals(meta.matchedPattern)) {
                return VARIABLES;

            } else if (Grammar.Class.PARAMETERS.equals(meta.matchedPattern)) {
                return PARAMETERS;

            }

            return CLASS;
        }

        @Override
        public void closeScope(StateContext context) {
            context.setClName(null);
        }
    },

    FUNCTION_DEFINITION(Grammar.FunctionDefinition.values()) {
        @Override
        public State getNewState (StateContext context, Grammar.LineMeta meta){

            if (Grammar.FunctionDefinition.ACTIONS.equals(meta.matchedPattern)) {
                return FUNCTION_BODY;
            } else if (Grammar.FunctionDefinition.LOCAL_VARIABLES.equals(meta.matchedPattern)) {
                return VARIABLES;
            } else if (Grammar.FunctionDefinition.PARAMETERS.equals(meta.matchedPattern)) {
                return PARAMETERS;
            } else
                return FUNCTION_DEFINITION;
        }

        @Override
        public void closeScope(StateContext context) {
            context.setFnName(null);
        }
    },

    VARIABLES(Grammar.FunctionDefinition.values()),

    PARAMETERS(Grammar.FunctionDefinition.values()),

    FUNCTION_BODY(Grammar.FunctionDefinition.values()),

    COMMENT_BLOCK(Grammar.NO_PATTERNS),

    COLLAPSED(Grammar.NO_PATTERNS);

    private Grammar.LinePattern[] grammar;
    private int indent;

    State(Grammar.LinePattern[] grammar) {
        this.grammar = grammar;
    }

    protected State getNewState(StateContext context, Grammar.LineMeta meta) {
        return context.getCurrentState();
    }

    public final State process (StateContext context, Grammar.LineMeta meta) {
        if (Grammar.General.COMMENT_BLOCK.equals(meta.matchedPattern)) {
            return COMMENT_BLOCK;
        }

        return getNewState(context, meta);
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public Grammar.LinePattern[] getGrammar() {
        return grammar;
    }

    public void closeScope(StateContext context) {
    }

    public String getAppName() {
        throw new RuntimeException("getAppName should not be used in this ");
    }

    public void setAppName(String appName) {
        throw new RuntimeException("setAppName should not be used in this ");
    }
}