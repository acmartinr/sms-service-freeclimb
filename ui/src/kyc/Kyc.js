import * as React from 'react';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Box from '@material-ui/core/Box';
import BusinessProfile from "./BusinessProfile";
import CampaignRegistry from "./CampaignRegistry";
import KycInfoAPI from "./KycInfoAPI";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import CircularProgress from "@material-ui/core/CircularProgress";

function TabPanel(props) {
  const {children, value, index, ...other} = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{p: 3}}>
          <span>{children}</span>
        </Box>
      )}
    </div>
  );
}

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    'aria-controls': `simple-tabpanel-${index}`,
  };
}

export default function Kyc({setSubtitle}) {
  const [state, setState] = React.useState({
    // tab 1
    useCaseId: 0,
    relevantAttributesIds: ["0"],
    campaignDescription: "",
    messageSample: "",
    // tab 0
    companyName: "",
    brandName: "",
    companyCountry: "",
    companyTaxId: "",
    businessTypeId: 0,
    businessIndustryId: 0,
    companyWebsite: "",
    companyStockExchange: "",
    companyAddressStreet: "",
    companyAddressZip: "",
    companyAddressCity: "",
    companyAddressState: "",
    companyAddressCountry: "",
    contactFirstName: "",
    contactLastName: "",
    contactPhone: "",
    contactEmail: "",
    billingEmail: "",
    contactTollFreePhone: "",
    supportEmail: "",
    businessPosition: "",
    businessTitle: "",
    confirmedProviderContact: false,
    confirmedTerms: false,
    completed: false
  });
  const [status, setStatus] = React.useState({
    errorRequest: false,
    loading: true,
    currentTab: 0,
    isInitialized: false
  });

  const setLoading = (value) => setStatus({
    ...status, loading: value
  });

  React.useEffect(() => {
    const initializeBusinessProfileInfo = async () => {
      const response = await KycInfoAPI.getKycInfo();
      let initialStatus = {...status};
      if (response.status === 'OK' && response.data) {
        let initialState = response.data;
        initialState.relevantAttributesIds = initialState.relevantAttributesIds.split(',');
        initialStatus.errorRequest = false;
        setState(initialState);
      } else if (response.error) {
        initialStatus.errorRequest = true;
      }
      initialStatus.isInitialized = true;
      initialStatus.loading = false;
      setStatus(initialStatus);
    };
    initializeBusinessProfileInfo();
  }, []);

  React.useEffect(() => {
    const updateOrInsertCampaignRegistryInfo = async () => {
      if (status.isInitialized && !status.loading) {
        setLoading(true);
        let updatedStatus = {...status};
        const request = {...state, relevantAttributesIds: state.relevantAttributesIds.toString()}
        if (state.companyName && state.campaignDescription) {
          request.completed = true;
        }
        await KycInfoAPI.updateKycInfo(request,
          (response) => {
            if (response.status === 'OK' && response.data) {
              let updatedState = response.data;
              updatedState.relevantAttributesIds = updatedState.relevantAttributesIds.split(',');
              updatedStatus.errorRequest = false;
              setState(updatedState);
              setStatus(updatedStatus);
            } else if (response.error) {
              updatedStatus.errorRequest = true;
            }
            updatedStatus.loading = false;
            setStatus(updatedStatus);
          });
      }
    };
    updateOrInsertCampaignRegistryInfo();
  }, [state])

  const handleChange = (event, newValue) =>
    setStatus({...status, currentTab: newValue});

  const onUpdate = (values) => {
    setState({...state, ...values});
  }

  React.useEffect(() => {
    if (status.isInitialized && !status.loading) {
      if (state.completed) {
        setSubtitle("Completed");
      } else setSubtitle("Not complete");
    }
  }, [status]);


  return (
    <Box sx={{width: '100%'}}>
      <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
        <Tabs value={status.currentTab} onChange={handleChange} aria-label="basic tabs example"
              indicatorColor="primary">
          <Tab label="Business Profile" {...a11yProps(0)} />
          <Tab label="Campaign Registry" {...a11yProps(1)} />
        </Tabs>
      </Box>
      {status.errorRequest ?
        <Typography color="error" component="h5" align="center" className='padding-top-10' variant="subtitle2">
          There was an error with the request. Please try again later.
        </Typography> :
        status.loading ?
          <Grid item style={{textAlign: 'center', marginTop: 10}}> <CircularProgress/>
          </Grid> :
          <div>
            <TabPanel value={status.currentTab} index={0}>
              <BusinessProfile inputValues={state} onUpdate={onUpdate}/>
            </TabPanel>
            <TabPanel value={status.currentTab} index={1}>
              <CampaignRegistry inputValues={state} onUpdate={onUpdate}/>
            </TabPanel>
          </div>
      }
    </Box>
  );
}
