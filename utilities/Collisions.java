package utilities;

public class Collisions {

    private final int B_WIDTH = 800;                //valtozok deklaralasa es inicializalasa a JPanel es Player mereteivel
    private final int B_HEIGHT = 800;

    private Player one = new Player();
    private Player two = new Player();

    private boolean inGame = true;


    public Collisions() {
        for (int i = 0; i < one.getDots(); i++) {

            if ((i > 4) && (one.getX()[0] == one.getX()[i]
                    && (one.getY()[0] == one.getY()[i]))) {                           //itt lehetett volna a ket if()-et egybe is irni
                inGame = false;                                       //de az atlathatosag kedveert igy irtam, kulon a two
            }                                                         //es kulon a one koordinataira, a fejere es a vegere

            for (int j = 0; j < two.getDots(); j++) {
                if (one.getX()[j] == two.getX()[i]
                        && one.getY()[j] == two.getY()[i])
                    inGame = false;
            }
        }

        for (int i = 0; i < two.getDots(); i++) {
            if ((i > 4) && (two.getX()[0] == two.getX()[i])
                    && (two.getY()[0] == two.getY()[i])) {
                inGame = false;
            }

            for (int j = 0; j < one.getDots(); j++) {
                if (one.getX()[j] == two.getX()[i]
                        && one.getY()[j] == two.getY()[i])
                    inGame = false;
            }
        }

        if (one.getY()[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (one.getY()[0] < 0) {
            inGame = false;
        }

        if (one.getX()[0] >= B_WIDTH) {
            inGame = false;
        }

        if (one.getX()[0] < 0) {
            inGame = false;
        }

        if (two.getY()[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (two.getY()[0] < 0) {
            inGame = false;
        }

        if (two.getX()[0] >= B_WIDTH) {
            inGame = false;
        }

        if (two.getX()[0] < 0) {
            inGame = false;
        }

    }

    public boolean getCondition(){
        return inGame;
    }
};


