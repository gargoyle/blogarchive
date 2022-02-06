package uk.co.paulcourt.blog.security;

/**
 *
 * @author paul
 */
public class JwtFactory {

//    private PrivateKey privateKey;
//    private PublicKey publicKey;
//    private String issuer;
//
//    public JwtFactory(Config config) {
//        loadPrivateKey(config.get("private-key").asString().get());
//        loadPublicKey(config.get("public-key").asString().get());
//        this.issuer = config.get("issuer").asString().get();
//    }
//
//    private void loadPrivateKey(String filename) throws RuntimeException {
//        try {
//            URL fullPath = getClass().getClassLoader().getResource(filename);
//            InputStream is = fullPath.openStream();
//            byte[] keyBytes = is.readAllBytes();
//            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//            KeyFactory kf = KeyFactory.getInstance("RSA");
//            privateKey = kf.generatePrivate(spec);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load private key!", e);
//        }
//    }
//
//    private void loadPublicKey(String filename) throws RuntimeException {
//        try {
//            URL fullPath = getClass().getClassLoader().getResource(filename);
//            InputStream is = fullPath.openStream();
//            byte[] keyBytes = is.readAllBytes();
//            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//            KeyFactory kf = KeyFactory.getInstance("RSA");
//            publicKey = kf.generatePublic(spec);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load public key!", e);
//        }
//    }
//
//    public PublicKey getPublicKey() {
//        return publicKey;
//    }
//
//    public String generateJwsForUser(UserDTO user) {
//        String jws = Jwts.builder()
//                .setIssuer(this.issuer)
//                .setIssuedAt(new Date())
//                .setSubject(user.getId())
//                .claim("nickname", user.getNickname())
//                .claim("roles", user.getRoles())
//                .signWith(privateKey)
//                .compact();
//
//        return jws;
//    }
//
//    public Jws<Claims> verifyUserJws(String originalJws) {
//        Jws<Claims> parsedJws = Jwts.parser()
//                .requireIssuer(this.issuer)
//                .setSigningKey(publicKey)
//                .parseClaimsJws(originalJws);
//        return parsedJws;
//    }
}
