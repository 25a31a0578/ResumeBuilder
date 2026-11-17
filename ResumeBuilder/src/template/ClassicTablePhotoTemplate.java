package template;

/** Template 4: Classic + education table + photo. */
public class ClassicTablePhotoTemplate extends ClassicTableTemplate {
    @Override protected boolean usesPhoto() { return true; }
    @Override public String getName() { return "4 - Classic + Table + Photo"; }
}
