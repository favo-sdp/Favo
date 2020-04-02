package ch.epfl.favo;

import ch.epfl.favo.favor.Favor;

public class FakeItemFactory {

    public static Favor getFavor(){
        return new Favor(TestConstants.TITLE,TestConstants.DESCRIPTION,
                TestConstants.REQUESTER_ID,TestConstants.LOCATION,TestConstants.FAVOR_STATUS);
    }
}
