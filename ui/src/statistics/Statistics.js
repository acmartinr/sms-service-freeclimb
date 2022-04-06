import React, {Component} from 'react';

import './Statistics.css';
import '../common/Common.css';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

import Container from '@material-ui/core/Container';
import Grid from '@material-ui/core/Grid';

class Statistics extends Component {
  constructor(props) {
    super(props);

    this.state = {
      statistics: {},
      loaded: false
    };
  }

  componentDidMount() {
    this.updateStatistics();
  }

  updateStatistics() {
    CampaignsAPI.getStatistics({userId: Session.getUser().id},
    response => {
      if (response.status === "OK") {
        this.setState({
          statistics: response.data,
          loaded: true
        });
      }
    });
  }

  render() {
    return (
      <div className="applications-content">
        <Container maxWidth="lg" className="applications-container">
          <Grid container spacing={3}>
            <Grid item xs={4} md={4} lg={4}>
              <h1 className='center'>{this.state.loaded ? this.state.statistics.active : '-'}</h1>
              <h4 className='center'>Active Campaigns</h4>
            </Grid>
            <Grid item xs={4} md={4} lg={4}>
              <h1 className='center'>{this.state.loaded ? this.state.statistics.total : '-'}</h1>
              <h4 className='center'>Total Campaigns</h4>
            </Grid>
            <Grid item xs={4} md={4} lg={4}>
              <h1 className='center'>{this.state.loaded ? this.state.statistics.sent : '-'}</h1>
              <h4 className='center'>SMS sent</h4>
            </Grid>
          </Grid>
        </Container>
      </div>
    )
  }
}

export default Statistics;
