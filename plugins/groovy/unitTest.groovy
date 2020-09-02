// Все параметры, которые передаются извне и отправляются, т.е. vars и properties не объявляются.
// Они считаются уже присутствующими. Если объявить с типам (String s;), то это будет внутренняя переменная.
v1 = var1;
var1 = "new war str";
v2 = var2;
var2 = 10L;
v3 = var3;

v_str = "some str";
v_int = 200;

class TestClass {

    boolean checkP1(String p1) {
        return p1 != 'prop'
    }

}

println var1;
println var2;
println var3;


println p1;
if (new TestClass().checkP1(p1)) {
    return "p1 != prop";
}
p1 = "new P1"
println p2;
println p3;


if (p2 == 2) {
    return ru.apertum.qsystem.server.model.QServiceTree.getInstance().getRoot().getId();
} else {
    return "test success!";
}



