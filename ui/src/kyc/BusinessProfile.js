import React from 'react';
import * as Yup from "yup";
import {Form, Formik} from "formik";
import './Kyc.css';
import '../common/Common.css'
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import {
  Box,
  Checkbox,
  FormControlLabel,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  TextField
} from "@material-ui/core";
import Container from "@material-ui/core/Container";
import Button from "@material-ui/core/Button";

const industries = ["Electronics", "Energy", "Fast Moving Consumer Goods", "Financial", "Fintech", "Food And Beverage", "Government", "Healthcare", "Hospitality", "Insurance", "Jewelry", "Legal", "Manufacturing", "Media", "Not For Profit", "Oil And Gas", "Online", "Raw Materials", "Real Estate", "Religion", "Retail", "Technology", "Telecommunications", "Transportation", "Travel"]

function BusinessProfile({onUpdate, inputValues}) {

  const validationSchema = Yup.object(
    {
      companyName: Yup.string().required('Required'),
      brandName: Yup.string().required('Required'),
      companyCountry: Yup.string().required('Required'),
      companyTaxId: Yup.string().required('Required'),
      businessTypeId: Yup.number().min(1, 'Select an option'),
      businessIndustryId: Yup.number().min(1, 'Select an option'),
      companyWebsite: Yup.string().matches(
        /((https?):\/\/)?(www.)?[a-z0-9]+(\.[a-z]{2,}){1,3}(#?\/?[a-zA-Z0-9#]+)*\/?(\?[a-zA-Z0-9-_]+=[a-zA-Z0-9-%]+&?)?$/,
        'Must be a valid website URL'
      )
        .required('Please enter the website URL'),
      companyStockExchange: Yup.string(),
      companyAddressStreet: Yup.string().required('Required'),
      companyAddressZip: Yup.string().matches(/^([0-9]{5}|[A-Z][0-9][A-Z] ?[0-9][A-Z][0-9])$/, 'Must be a valid postal code').required('Required'),
      companyAddressCity: Yup.string().required('Required'),
      companyAddressState: Yup.string().required('Required'),
      companyAddressCountry: Yup.string().required('Required'),
      contactFirstName: Yup.string().required('Required'),
      contactLastName: Yup.string().required('Required'),
      contactPhone: Yup.string().matches(/\(?([0-9]{3})\)?[-.●]?([0-9]{3})[-.●]?([0-9]{4})$/, 'Must be a valid phone number').required('Required'),
      contactEmail: Yup.string().email('Must be a valid email').required('Required'),
      billingEmail: Yup.string().email('Must be a valid email').required('Required'),
      contactTollFreePhone: Yup.string().matches(/\(?([0-9]{3})\)?[-.●]?([0-9]{3})[-.●]?([0-9]{4})$/, 'Must be a valid phone number').required('Required'),
      supportEmail: Yup.string().email('Must be a valid email').required('Required'),
      confirmedProviderContact: Yup.boolean().oneOf([true], "You must accept the terms and conditions").required(),
      confirmedTerms: Yup.boolean().oneOf([true], "You must accept the terms and conditions").required(),
      businessPosition: Yup.string().required('Required'),
      businessTitle: Yup.string().required('Required')
    });

  const [state, setState] = React.useState({
    isEditing: false,
  });

  const onSubmit = (values) => {
    let stateChanged = false;
    for (const [key, value] of Object.entries(values)) {
      if (value !== inputValues[key]) {
        stateChanged = true
        break;
      }
    }
    if (stateChanged) onUpdate(values);
  }

  return (
    <Formik
      initialValues={inputValues}
      validationSchema={validationSchema}
      onSubmit={async (values, {resetForm}) => {
        await onSubmit(values);
        resetForm();
        setState({isEditing: false});
      }}
      enableReinitialize={true}
    >
      {({
          values,
          touched,
          errors,
          handleChange,
          handleBlur,
          handleSubmit,
          isValid,
          setFieldValue
        }) => (
        <Container style={{padding: 20}} maxWidth="lg" sx={{mt: 4, mb: 4}}>
          <Form onSubmit={handleSubmit} noValidate>
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
              }}
            >
              <Typography>
                Business Information
              </Typography>
              <Box>
                <Grid container spacing={3}>
                  <Grid item xs={12} md={4}>
                    <TextField
                      style={{marginTop: 3}}
                      disabled={!state.isEditing}
                      fullWidth
                      margin="dense"
                      name="companyName"
                      required
                      id="companyName"
                      label="Legal Company Name"
                      value={values.companyName}
                      onChange={handleChange}
                      error={touched.companyName && !!errors.companyName}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyName && errors.companyName
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="brandName"
                      label="DBA or Brand name (if different)"
                      name="brandName"
                      value={values.brandName}
                      onChange={handleChange}
                      error={touched.brandName && !!errors.brandName}
                      onBlur={handleBlur}
                      helperText={
                        touched.brandName && errors.brandName
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="companyCountry"
                      label="Country of Registration"
                      name="companyCountry"
                      value={values.companyCountry}
                      onChange={handleChange}
                      error={touched.companyCountry && !!errors.companyCountry}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyCountry && errors.companyCountry
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <InputLabel style={{fontSize: 11, marginTop: 5}} required id="businessTypeLabel">Legal
                      Form of the
                      company</InputLabel>
                    <Select
                      disabled={!state.isEditing}
                      required
                      labelId="businessTypeLabel"
                      id="businessTypeId"
                      fullWidth
                      value={values.businessTypeId}
                      label="Legal Form of the company"
                      placeholder="Legal Form of the company"
                      onChange={(event) => {
                        setFieldValue('businessTypeId', event.target.value)
                      }}
                      error={touched.businessTypeId && !!errors.businessTypeId}
                      onBlur={handleBlur}>
                      <MenuItem disabled value={0}>Select...</MenuItem>
                      <MenuItem value={1}>Publicly Traded</MenuItem>
                      <MenuItem value={2}>Private Corporation</MenuItem>
                      <MenuItem value={3}>Individual</MenuItem>
                      <MenuItem value={4}>Charity/Non-Profit</MenuItem>
                    </Select>
                    <FormHelperText
                      error={true}>{touched.businessTypeId && errors.businessTypeId}</FormHelperText>
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <InputLabel style={{fontSize: 11, marginTop: 5}} required id="businessIndustryLabel">Business
                      industry</InputLabel>
                    <Select
                      disabled={!state.isEditing}
                      required
                      labelId="businessIndustryLabel"
                      id="businessIndustryId"
                      name="businessIndustryId"
                      fullWidth
                      value={values.businessIndustryId}
                      label="Business industry"
                      placeholder="Business industry"
                      onChange={(event) => {
                        setFieldValue('businessIndustryId', event.target.value)
                      }}
                      error={touched.businessIndustryId && !!errors.businessIndustryId}
                      onBlur={handleBlur}
                    >
                      <MenuItem disabled value={0}>Select...</MenuItem>
                      {
                        industries.map((industry, index) => <MenuItem key={index + 1}
                                                                      value={index + 1}>{industry}</MenuItem>)
                      }
                    </Select>
                    <FormHelperText
                      error={true}>{touched.businessIndustryId && errors.businessIndustryId}</FormHelperText>
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="companyTaxId"
                      label="Tax ID/EIN of the company"
                      name="companyTaxId"
                      value={values.companyTaxId}
                      onChange={handleChange}
                      error={touched.companyTaxId && !!errors.companyTaxId}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyTaxId && errors.companyTaxId
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="companyWebsite"
                      label="Company website"
                      type="url"
                      name="companyWebsite"
                      value={values.companyWebsite}
                      onChange={handleChange}
                      error={touched.companyWebsite && !!errors.companyWebsite}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyWebsite && errors.companyWebsite
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      fullWidth
                      id="companyStockExchange"
                      label="Stock Exchange (if applicable)"
                      name="companyStockExchange"
                      value={values.companyStockExchange}
                      onChange={handleChange}
                      error={touched.companyStockExchange && !!errors.companyStockExchange}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyStockExchange && errors.companyStockExchange
                      }
                    />
                  </Grid>
                </Grid>
              </Box>
            </Box>
            <Box
              sx={{
                marginTop: 20,
                display: 'flex',
                flexDirection: 'column',
              }}
            >
              <Typography>
                Business Address
              </Typography>
              <Box>
                <Grid container spacing={3}>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      fullWidth
                      name="companyAddressStreet"
                      required
                      id="companyAddressStreet"
                      label="Street Address"
                      value={values.companyAddressStreet}
                      onChange={handleChange}
                      error={touched.companyAddressStreet && !!errors.companyAddressStreet}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyAddressStreet && errors.companyAddressStreet
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      fullWidth
                      name="companyAddressZip"
                      required
                      id="companyAddressZip"
                      label="Postal Code"
                      value={values.companyAddressZip}
                      onChange={handleChange}
                      error={touched.companyAddressZip && !!errors.companyAddressZip}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyAddressZip && errors.companyAddressZip
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="companyAddressCity"
                      label="City"
                      name="companyAddressCity"
                      autoComplete="city"
                      value={values.companyAddressCity}
                      onChange={handleChange}
                      error={touched.companyAddressCity && !!errors.companyAddressCity}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyAddressCity && errors.companyAddressCity
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="companyAddressState"
                      label="State"
                      name="companyAddressState"
                      autoComplete="state"
                      value={values.companyAddressState}
                      onChange={handleChange}
                      error={touched.companyAddressState && !!errors.companyAddressState}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyAddressState && errors.companyAddressState
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="companyAddressCountry"
                      label="Country"
                      name="companyAddressCountry"
                      autoComplete="country"
                      value={values.companyAddressCountry}
                      onChange={handleChange}
                      error={touched.companyAddressCountry && !!errors.companyAddressCountry}
                      onBlur={handleBlur}
                      helperText={
                        touched.companyAddressCountry && errors.companyAddressCountry
                      }
                    />
                  </Grid>
                </Grid>
              </Box>
            </Box>
            <Box
              sx={{
                marginTop: 20,
                display: 'flex',
                flexDirection: 'column',
              }}
            >
              <Typography>
                Contact Info
              </Typography>
              <Box>
                <Grid container spacing={3}>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      fullWidth
                      name="contactFirstName"
                      required
                      id="contactFirstName"
                      label="First Name"
                      autoComplete="given-name"
                      value={values.contactFirstName}
                      onChange={handleChange}
                      error={touched.contactFirstName && !!errors.contactFirstName}
                      onBlur={handleBlur}
                      helperText={
                        touched.contactFirstName && errors.contactFirstName
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      fullWidth
                      name="contactLastName"
                      required
                      id="contactLastName"
                      label="Last Name"
                      autoComplete="family-name"
                      value={values.contactLastName}
                      onChange={handleChange}
                      error={touched.contactLastName && !!errors.contactLastName}
                      onBlur={handleBlur}
                      helperText={
                        touched.contactLastName && errors.contactLastName
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={4}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="contactPhone"
                      label="Phone"
                      type="tel"
                      name="contactPhone"
                      autoComplete="phone"
                      value={values.contactPhone}
                      onChange={handleChange}
                      error={touched.contactPhone && !!errors.contactPhone}
                      onBlur={handleBlur}
                      helperText={
                        touched.contactPhone && errors.contactPhone
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="contactEmail"
                      label="Email"
                      type="email"
                      name="contactEmail"
                      autoComplete="email"
                      value={values.contactEmail}
                      onChange={handleChange}
                      error={touched.contactEmail && !!errors.contactEmail}
                      onBlur={handleBlur}
                      helperText={
                        touched.contactEmail && errors.contactEmail
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="billingEmail"
                      label="Billing email"
                      type="email"
                      name="billingEmail"
                      value={values.billingEmail}
                      onChange={handleChange}
                      error={touched.billingEmail && !!errors.billingEmail}
                      onBlur={handleBlur}
                      helperText={
                        touched.billingEmail && errors.billingEmail
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="businessTitle"
                      label="Business Title"
                      name="businessTitle"
                      value={values.businessTitle}
                      onChange={handleChange}
                      error={touched.businessTitle && !!errors.businessTitle}
                      onBlur={handleBlur}
                      helperText={
                        touched.businessTitle && errors.businessTitle
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="businessPosition"
                      label="Position at Company"
                      name="businessPosition"
                      value={values.businessPosition}
                      onChange={handleChange}
                      error={touched.businessPosition && !!errors.businessPosition}
                      onBlur={handleBlur}
                      helperText={
                        touched.businessPosition && errors.businessPosition
                      }
                    />
                  </Grid>
                </Grid>
              </Box>
            </Box>
            <Box
              sx={{
                marginTop: 20,
                display: 'flex',
                flexDirection: 'column',
              }}
            >
              <Typography>
                Consumer Service
              </Typography>
              <Box>
                <Grid container spacing={3}>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="contactTollFreePhone"
                      label="Customer support Toll-Free phone"
                      name="contactTollFreePhone"
                      type="tel"
                      value={values.contactTollFreePhone}
                      onChange={handleChange}
                      error={touched.contactTollFreePhone && !!errors.contactTollFreePhone}
                      onBlur={handleBlur}
                      helperText={
                        touched.contactTollFreePhone && errors.contactTollFreePhone
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      disabled={!state.isEditing}
                      required
                      fullWidth
                      id="supportEmail"
                      type="email"
                      label="Customer support email"
                      name="supportEmail"
                      value={values.supportEmail}
                      onChange={handleChange}
                      error={touched.supportEmail && !!errors.supportEmail}
                      onBlur={handleBlur}
                      helperText={
                        touched.supportEmail && errors.supportEmail
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={12}>
                    <FormControlLabel disabled={!state.isEditing} control={
                      <Checkbox checked={values.confirmedProviderContact}/>}
                                      id="confirmedProviderContact"
                                      name="confirmedProviderContact"
                                      value={values.confirmedProviderContact}
                                      onChange={handleChange}
                                      onBlur={handleBlur}
                                      label="I confirm that my nominated authorized representative agree to be contacted by service provider."/>
                    <FormHelperText
                      error={true}>{touched.confirmedProviderContact && errors.confirmedProviderContact}</FormHelperText>
                    <FormControlLabel disabled={!state.isEditing}
                                      id="confirmedTerms"
                                      name="confirmedTerms"
                                      control={
                                        <Checkbox checked={values.confirmedTerms}/>}
                                      onBlur={handleBlur}
                                      value={values.confirmedTerms}
                                      onChange={handleChange}
                                      label="I certify that the information provided is true and accurate. Customer acknowledges that service provider has the right to process information for the regulatory purposes and/or identity verification."/>
                    <FormHelperText
                      error={true}>{touched.confirmedTerms && errors.confirmedTerms}</FormHelperText>
                  </Grid>
                </Grid>
              </Box>
            </Box>
            <Box className='change-password-button-wrapper'>
              {
                state.isEditing ?
                  <Button className='margin-top-10' variant="contained"
                          type="submit"
                          disabled={!isValid}
                          color="primary">
                    Save
                  </Button> :
                  <Button className='margin-top-10' variant="contained"
                          color="primary"
                          onClick={(evt) => {
                            evt.preventDefault();
                            setState({...state, isEditing: true});
                          }}>
                    Edit
                  </Button>
              }
            </Box>
          </Form></Container>)}
    </Formik>
  )
}

export default BusinessProfile;
