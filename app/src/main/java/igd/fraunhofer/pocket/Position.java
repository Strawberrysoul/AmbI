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
            case "STANDING": res = R.drawable.ic_stand; break;
            case "LEFTTABLE": res = R.drawable.ic_lefttable; break;
            case "RIGHTTABLE": res = R.drawable.ic_righttable; break;
            case "LEFTKNEE": res = R.drawable.ic_leftknee; break;
            case "RIGHTKNEE": res = R.drawable.ic_rightknee; break;
            case "SITTING": res = R.drawable.ic_sitting; break;
            case "BACK": res = R.drawable.ic_back; break;
            case "FRONT": res = R.drawable.ic_front; break;
            default: res = R.drawable.unknown;
        }
        return res;
    }
}

enum PositionOptions {
    STANDING,SITTING,LEFTTABLE,RIGHTTABLE,BACK,FRONT,LEFTKNEE,RIGHTKNEE
}
