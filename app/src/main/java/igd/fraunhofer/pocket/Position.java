package igd.fraunhofer.pocket;

/**
 * Created by Malte Lenhart on 02.08.17.
 *
 * Class to represent the classes from which the classifier has to decide & associate images w/ them
 */
class Position {
    private PositionOptions pos;

    Position(PositionOptions pos){
        this.pos = pos;
    }

    void setPosition(PositionOptions pos){
        this.pos = pos;
    }

    int getPositionResource(){
        int res;
        switch (pos.toString()){
            case "STANDING": res = R.drawable.standing; break;
            case "LEFT": res = R.drawable.leanleft; break;
            case "RIGHT": res = R.drawable.leanright; break;
            case "SITTING": res = R.drawable.sitting; break;
            default: res = R.drawable.unknown;
        }
        return res;
    }
}

enum PositionOptions {
   STANDING,  LEFT, RIGHT, SITTING, PROXIMITY
}
