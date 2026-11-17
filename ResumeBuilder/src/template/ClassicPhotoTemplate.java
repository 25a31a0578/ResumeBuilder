package template;

/** Template 2: Classic + photo on the right of the header (only if a photo was chosen). */
public class ClassicPhotoTemplate extends ClassicTemplate {
    @Override protected boolean usesPhoto() { return true; }
    @Override public String getName() { return "2 - Classic + Photo"; }
}
