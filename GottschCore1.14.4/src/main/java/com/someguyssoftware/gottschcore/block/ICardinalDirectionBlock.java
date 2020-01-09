
// TODO should there be a lower-level interface for all directional blocks? because whether it is horizontal only or not
// it is still a PropertyDirection value.
public interface ICardinalDirectionBlock {

  /**
   * get the PropertyDirection
   */
  public PropertyDirection getFacingProperty();
  
  public EnumFacing getFacingEnum();
}
