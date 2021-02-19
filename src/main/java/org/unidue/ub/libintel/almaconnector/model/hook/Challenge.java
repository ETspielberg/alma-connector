package org.unidue.ub.libintel.almaconnector.model.hook;

public class Challenge {

    private String challenge;

    public Challenge() {this.challenge = "";}

    public Challenge(String challenge) {
        this.challenge = challenge;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }
}
