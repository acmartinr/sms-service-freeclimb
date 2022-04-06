import React from 'react';
import * as Yup from "yup";
import {Form, Formik} from "formik";
import './Kyc.css';
import '../common/Common.css'
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import {
  Box,
  Chip,
  FormHelperText,
  Input,
  InputLabel,
  MenuItem,
  Select,
  TextareaAutosize,
  TextField
} from "@material-ui/core";
import Container from "@material-ui/core/Container";
import Button from "@material-ui/core/Button";

const useCases = ["2FA", "Account Notifications", "Customer Care", "Delivery Notification", "Fraud Alert Messaging", "Higher Education", "Low Volume Mixed", "Marketing", "Mixed", "Polling and Voting", "Public Service Announcements", "Security Alerts", "Carrier Exemptions", "Charity", "Conversational Messaging", "Emergency", "Political", "Social", "Sweepstakes"];

const campaignAttributes = ["Subscriber Opt-In", "Subscriber Opt-Out", "Subscriber Help", "Number Pooling", "Direct Lending or Loan Arrangement", "Embedded Link", "Embedded Phone Number", "Affiliate Marketing", "Age-Gated Content"]

function getStyles(name, personName) {
  return {
    fontWeight:
      personName.indexOf(name) === -1
        ? 'normal'
        : 'medium'
  }
}

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

function CampaignRegistry({onUpdate, inputValues}) {

  const validationSchema = Yup.object(
    {
      useCaseId: Yup.number().min(1, 'Select an option'),
      relevantAttributesIds: Yup.array().of(Yup.string().matches(/[^0-9]+/, 'Select an option')).min(1, 'Select an option'),
      campaignDescription: Yup.string().required('Required'),
      messageSample: Yup.string().required('Required')
    });

  const [state, setState] = React.useState({
    isEditing: false
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
      onSubmit={(values, {resetForm}) => {
        onSubmit(values);
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
                Campaign Information
              </Typography>
              <Box>
                <Grid container spacing={3}>
                  <Grid item xs={12} md={12}>
                    <InputLabel style={{fontSize: 11, marginTop: 10}} required id="selectUseCaseLabel">Select
                      Use-case</InputLabel>
                    <Select
                      disabled={!state.isEditing}
                      required
                      labelId="selectUseCaseLabel"
                      id="useCaseId"
                      name="useCaseId"
                      fullWidth
                      value={values.useCaseId}
                      label="Select Use-case"
                      onChange={(event) => {
                        setFieldValue('useCaseId', event.target.value)
                      }}
                      error={touched.useCaseId && !!errors.useCaseId}
                      onBlur={handleBlur}
                    >
                      <MenuItem disabled value={0}>Select...</MenuItem>
                      {
                        useCases.map((useCase, index) => <MenuItem key={index + 1}
                                                                   value={index + 1}>{useCase}</MenuItem>)
                      }
                    </Select>
                    <FormHelperText
                      error={true}>{touched.useCaseId && errors.useCaseId}</FormHelperText>
                  </Grid>

                  <Grid item xs={12} md={12}>
                    <InputLabel style={{fontSize: 11, marginTop: 5, marginBottom: 5}}
                                id="attributes-multiple-chip-label">Select
                      all relevant campaign attributes</InputLabel>
                    <Select
                      onBlur={handleBlur}
                      labelId="attributes-multiple-chip-label"
                      id="relevantAttributesIds"
                      disabled={!state.isEditing}
                      multiple
                      required
                      fullWidth
                      value={values.relevantAttributesIds}
                      onChange={(event) => {
                        const {
                          target: {value},
                        } = event;
                        const index = value.indexOf("0");
                        if (index > -1) {
                          value.splice(index, 1);
                        }
                        setFieldValue('relevantAttributesIds', typeof value === 'string' ? value.split(',') : value)
                      }}
                      input={<Input id="select-multiple-chip"
                                    label="Select all relevant campaign attributes"/>}
                      renderValue={(selected) => {
                        if (selected[0] === "0") {
                          return <label>Select...</label>;
                        }
                        return <Box sx={{display: 'flex', flexWrap: 'wrap', gap: 0.5}}>
                          {selected.filter(value => value !== "0").map((value) => (
                            <Chip disabled={!state.isEditing} key={value} label={value}/>
                          ))}
                        </Box>
                      }
                      }
                      MenuProps={MenuProps}
                      error={!!errors.relevantAttributesIds}
                    >
                      {campaignAttributes.map((attribute) => (
                        <MenuItem
                          key={attribute}
                          value={attribute}
                          style={getStyles(attribute, values.relevantAttributesIds)}
                        >
                          {attribute}
                        </MenuItem>
                      ))}
                    </Select>
                    <FormHelperText
                      error={true}>{errors.relevantAttributesIds}</FormHelperText>
                  </Grid>
                  <Grid item xs={12} md={12}>
                    <TextField
                      disabled={!state.isEditing}
                      fullWidth
                      name="campaignDescription"
                      required
                      id="campaignDescription"
                      label="Please give a description of the campaign"
                      value={values.campaignDescription}
                      onChange={handleChange}
                      error={touched.campaignDescription && !!errors.campaignDescription}
                      onBlur={handleBlur}
                      helperText={
                        touched.campaignDescription && errors.campaignDescription
                      }
                    />
                  </Grid>
                  <Grid item xs={12} md={12}>
                    <InputLabel style={{fontSize: 11, marginTop: 5, marginBottom: 5}}
                                id="sample-message-multiple-chip-label">Please provide sample messages (minimum
                      3)</InputLabel>
                    <TextareaAutosize
                      aria-label="empty textarea"
                      placeholder="Your answer..."
                      style={{width: '100%', height: 150}}
                      disabled={!state.isEditing}
                      name="messageSample"
                      required
                      id="messageSample"
                      label="Please provide sample messages (minimum 3)"
                      value={values.messageSample}
                      onChange={handleChange}
                      error={touched.messageSample && errors.messageSample}
                      onBlur={handleBlur}
                    />
                    <FormHelperText
                      error={true}>{touched.messageSample && errors.messageSample}</FormHelperText>
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
          </Form>
        </Container>
      )}
    </Formik>
  )
}

export default CampaignRegistry;
