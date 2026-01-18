package dev.tuples.simplecaptcha;

import java.util.Map;

public final class CaptchaChallenge {
    public String text;
    public String difficulty;
    public Map<String, Boolean> images;
}
